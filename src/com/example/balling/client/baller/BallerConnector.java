package com.example.balling.client.baller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.balling.Baller;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VDateField;
import com.vaadin.client.ui.VFilterSelect;
import com.vaadin.client.ui.VLabel;
import com.vaadin.client.ui.VPasswordField;
import com.vaadin.client.ui.VSlider;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;

@Connect(Baller.class)
public class BallerConnector extends AbstractExtensionConnector {

    BallerServerRpc rpc = RpcProxy.create(BallerServerRpc.class, this);

    private ComponentConnector extensionTarget;

    private List<Movable> objectsMoving = new ArrayList<Movable>();
    private Map<Widget, Movable> widgetToObjectMap = new HashMap<Widget, Movable>();

    private BallerEventHandler ballerEventHandler = null;

    private ArrayList<Class<? extends Widget>> classesAllowed = new ArrayList<Class<? extends Widget>>();

    private int lastX;

    private int lastY;

    private Timer timer = null;

    private int fps = 20;

    private final double gravity = 30.81 / fps;

    private double mouseSpeedFactor = 6d;

    private String rotationString;

    private String rotationStringBrowserSpecific;

    private int touchRecord = 0;

    private class Movable {

        private final Widget widget;
        private double speedX = 0;
        private double speedY = 0;

        private double rotationDegrees = 0;

        private double rotationSpeed = 0;

        public final int MAX_IMMUNITY_FRAMES = 7;

        private int immunityFrames = MAX_IMMUNITY_FRAMES;
        private final Element wrapper;
        private com.google.gwt.dom.client.Element parentElement;
        private final Element placeHolder;
        private int touches = 0;

        public Movable(Element placeHolder, Element wrapper, Widget widget,
                double speedX, double speedY) {
            this.placeHolder = placeHolder;
            this.wrapper = wrapper;
            this.widget = widget;
            setSpeedX(speedX);
            setSpeedY(speedY);
            rotationStringBrowserSpecific = "transform";
            String prefix = getBrowserPrefix();
            if (prefix != null) {
                rotationStringBrowserSpecific = getBrowserPrefix()
                        + "Transform";
            }

        }

        private String getBrowserPrefix() {
            String prefix = null;
            final BrowserInfo bi = BrowserInfo.get();
            if (bi.isWebkit()) {
                prefix = "webkit";
            } else if (bi.isIE()) {
                prefix = "ms";
            } else if (bi.isOpera()) {
                prefix = "O";
            } else if (bi.isFirefox()) {
                prefix = "Moz";
            }
            return prefix;
        }

        public double getSpeedY() {
            return speedY;
        }

        public void setSpeedY(double speedY) {
            this.speedY = speedY;
        }

        public double getSpeedX() {
            return speedX;
        }

        public void setSpeedX(double speedX) {
            this.speedX = speedX;
        }

        public Widget getWidget() {
            return widget;
        }

        public int getImmunityFrames() {
            return immunityFrames;
        }

        public void setImmunityFrames(int immunityFrames) {
            this.immunityFrames = immunityFrames;
        }

        public double getRotationDegrees() {
            return rotationDegrees;
        }

        public void setRotationDegrees(double rotationDegrees) {
            this.rotationDegrees = rotationDegrees;
        }

        public double getRotationSpeed() {
            return rotationSpeed;
        }

        public void setRotationSpeed(double rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
        }

        public Element getWrapper() {
            return wrapper;
        }

        public void setOriginalParentElement(
                com.google.gwt.dom.client.Element parentElement) {
            this.parentElement = parentElement;

        }

        public com.google.gwt.dom.client.Element getOriginalParentElement() {
            return parentElement;
        }

        public Element getPlaceHolder() {
            return placeHolder;
        }

        public void addOneTouch() {
            touches++;
        }

        public int getTouches() {
            return touches;
        }

    }

    @Override
    public BallerState getState() {
        return (BallerState) super.getState();
    }

    // second option
    public synchronized void beAfraid() {

    }

    public synchronized void animate() {

        List<Movable> toRemove = new ArrayList<BallerConnector.Movable>();
        for (Movable obj : objectsMoving) {
            Widget w = obj.widget;
            obj.setSpeedY(obj.getSpeedY() + gravity);

            obj.getWrapper()
                    .getStyle()
                    .setLeft(
                            obj.getWrapper().getAbsoluteLeft()
                                    + obj.getSpeedX(), Unit.PX);
            obj.getWrapper()
                    .getStyle()
                    .setTop(obj.getWrapper().getAbsoluteTop() + obj.getSpeedY(),
                            Unit.PX);

            obj.setRotationDegrees(obj.getRotationDegrees()
                    + obj.getRotationSpeed());

            w.getElement()
                    .getStyle()
                    .setProperty(rotationStringBrowserSpecific,
                            "rotate(" + obj.getRotationDegrees() + "deg)");

            if (obj.getWrapper().getAbsoluteLeft() + w.getOffsetWidth() <= -200
                    || obj.getWrapper().getAbsoluteTop()
                            + obj.getWrapper().getOffsetHeight() > extensionTarget
                            .getWidget().getOffsetHeight() + 600) {
                toRemove.add(obj);
            }
            obj.setImmunityFrames(obj.getImmunityFrames() - 1);
        }

        if (toRemove.size() > 0) {
            for (Movable obj : toRemove) {
                obj.getWidget()
                        .getElement()
                        .getStyle()
                        .setProperty(rotationStringBrowserSpecific,
                                "rotate(" + 0 + "deg)");
                widgetToObjectMap.remove(obj.getWidget());
                obj.getWidget().getElement().getStyle().setLeft(0, Unit.PX);
                obj.getWidget().getElement().getStyle().setTop(0, Unit.PX);
                // Adding back to parent
                obj.getOriginalParentElement()
                        .removeChild(obj.getPlaceHolder());
                obj.getOriginalParentElement().appendChild(
                        obj.getWidget().getElement());
                obj.getWidget().getElement().getStyle()
                        .setPosition(Position.RELATIVE);
                boolean isRecord = false;
                if (obj.getTouches() > touchRecord) {
                    touchRecord = obj.getTouches();
                    ballerEventHandler.onHitRecord(touchRecord);
                }

            }
            objectsMoving.removeAll(toRemove);

        }

        if (objectsMoving.size() == 0) {
            timer.cancel();
            timer = null;

        }
    }

    private boolean isHitFromUpside(Movable movable, int mouseSpeedY,
            double mSpeedFactor) {
        return movable.getSpeedY() - mouseSpeedY * mSpeedFactor < 0;
    }

    /**
     * Calculates rotation and returns recoil factor. If recoil factor is 0, it
     * will continue the same path
     */

    /*
     * These calculation are ONLY for demo purposes, calculating real paths,
     * rotations and to take frame rate correctly into account, would take too
     * much of my time at this point.
     */
    private double calculateRotationSpeed(Movable movable, int mouseSpeedX,
            int mouseSpeedY, int mousePosX, int mousePosY, double mSpeedFactor) {
        Widget w = movable.getWidget();
        double width = w.getOffsetWidth();
        double hitPointX = mousePosX - w.getAbsoluteLeft();
        boolean leftSideHit = (hitPointX - width / 2) < 0;
        boolean hitFromUpside = isHitFromUpside(movable, mouseSpeedY,
                mSpeedFactor);
        double hitPointXAbs = Math.abs(hitPointX - width / 2);
        // If hitting the middle, no rotation. At the end, full rotation
        double speedHitPowerY = movable.getSpeedY() - mouseSpeedY
                * mSpeedFactor;
        double lengthInertiaFactor = (40 / (width + 5));

        double rotationSpeed = Math.abs(lengthInertiaFactor * speedHitPowerY
                * (hitPointXAbs / (width / 2)));
        // false is clockwise
        movable.setRotationSpeed((leftSideHit ^ !hitFromUpside) ? (-rotationSpeed + movable
                .getRotationSpeed()) : (rotationSpeed + movable
                .getRotationSpeed()));

        // lets have at least 0.2 recoil, just for the sake of demoing purposes
        double recoil = 0.2 + 0.8 * (1 - (hitPointXAbs / (width / 2)));

        /*
         * System.out.println("ROTATION TESTING: mouseSpeedY: " + (mouseSpeedY *
         * mSpeedFactor) + "  object speed y: " + movable.getSpeedY() +
         * " leftSideHit: " + leftSideHit + " hit from upside: " + hitFromUpside
         * + "  (leftSideHit ^ !hitFromUpside): " + (leftSideHit ^
         * !hitFromUpside) + " widget width: " + width + " rotation speed: " +
         * rotationSpeed); System.out.println("Recoil returned:: " + recoil);
         * System.out.println("**********************");
         */
        return recoil;

    }

    // Y grows downwards, X grows to the right
    public synchronized void addToMoveAround(Widget w, int mouseSpeedX,
            int mouseSpeedY, int mousePosX, int mousePosY) {
        Movable movable = widgetToObjectMap.get(w);

        if (movable != null) {
            if (movable.getImmunityFrames() > 0) {
                return;
            }

        } else {
            Element placeHolder = DOM.createDiv();
            placeHolder.getStyle().setWidth(w.getOffsetWidth(), Unit.PX);
            placeHolder.getStyle().setHeight(w.getOffsetHeight(), Unit.PX);

            // Wrapper used so that the coordinate system doesn't change
            Element wrapper = DOM.createDiv();
            wrapper.getStyle().setLeft(w.getAbsoluteLeft(), Unit.PX);
            wrapper.getStyle().setTop(w.getAbsoluteTop(), Unit.PX);
            com.google.gwt.dom.client.Element parentElement = w.getElement()
                    .getParentElement();
            parentElement.appendChild(placeHolder);
            placeHolder.appendChild(wrapper);
            w.getElement().removeFromParent();
            wrapper.appendChild(w.getElement());
            wrapper.getStyle().setPosition(Position.FIXED);

            movable = new Movable(placeHolder, wrapper, w, 0, 0);
            movable.setOriginalParentElement(parentElement);
            objectsMoving.add(movable);
            widgetToObjectMap.put(w, movable);

            // kolla
        }

        // Speed & rotation calculations. NOTE! These are just made "for fun".
        // There was no intention of doing this the correct way (i.e.
        // using vectors for speeds, for example)
        movable.setImmunityFrames(movable.MAX_IMMUNITY_FRAMES);
        movable.addOneTouch();

        double recoil = calculateRotationSpeed(movable, mouseSpeedX,
                mouseSpeedY, mousePosX, mousePosY, mouseSpeedFactor);
        movable.setSpeedX(mouseSpeedX * mouseSpeedFactor / 3);

        if (isHitFromUpside(movable, mouseSpeedY, mouseSpeedFactor)) {
            movable.setSpeedY(movable.getSpeedY() + recoil
                    * (mouseSpeedY * (mouseSpeedFactor / 2)));
        } else {
            movable.setSpeedY(movable.getSpeedY()
                    - recoil
                    * (2 * movable.getSpeedY() - mouseSpeedY
                            * (mouseSpeedFactor / 2)));
        }

        ballerEventHandler.onObjectHit(mousePosX, mousePosY,
                movable.getTouches());

        if (timer == null) {
            timer = new Timer() {

                @Override
                public void run() {
                    animate();
                }
            };
            timer.scheduleRepeating(1000 / fps);
        }

    }

    private Class<? extends Widget> mapAllowedWidget(AllowedWidgets widgetType) {
        if (widgetType == AllowedWidgets.BUTTON) {
            return VButton.class;
        } else if (widgetType == AllowedWidgets.TEXTFIELD) {
            return VTextField.class;
        } else if (widgetType == AllowedWidgets.PASSWORDFIELD) {
            return VPasswordField.class;
        } else if (widgetType == AllowedWidgets.LABEL) {
            return VLabel.class;
        } else if (widgetType == AllowedWidgets.CAPTION) {
            return VCaption.class;
        } else if (widgetType == AllowedWidgets.FILTERSELECT) {
            return VFilterSelect.class;
        } else if (widgetType == AllowedWidgets.DATEFIELD) {
            return VDateField.class;
        } else if (widgetType == AllowedWidgets.SLIDER) {
            return VSlider.class;
        }

        return null;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        classesAllowed.clear();
        for (AllowedWidgets allowedOne : getState().allowedWidgets) {
            Class<? extends Widget> widget = mapAllowedWidget(allowedOne);
            if (widget == null) {
                continue;
            }
            classesAllowed.add(widget);
        }

    }

    @Override
    protected void extend(ServerConnector target) {

        ballerEventHandler = new BallerEventHandler() {

            @Override
            public void onObjectHit(int x, int y, int hits) {
                if (getState().hitHandlerEnabled) {
                    getRpcProxy(BallerServerRpc.class).objectHit(x, y, hits);
                }
            }

            @Override
            public void onHitRecord(int hits) {
                if (getState().recordHandlerEnabled) {
                    getRpcProxy(BallerServerRpc.class).hitRecord(hits);
                }
            }
        };
        extensionTarget = ((ComponentConnector) target);
        extensionTarget.getWidget().sinkEvents(Event.ONMOUSEOVER);
        extensionTarget.getWidget().getElement().getStyle()
                .setOverflow(Overflow.HIDDEN);
        extensionTarget.getWidget().addDomHandler(new MouseMoveHandler() {

            @SuppressWarnings("unchecked")
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (!getState().isWicked) {
                    return;
                }

                int mousex = event.getClientX();
                int mousey = event.getClientY();

                com.google.gwt.user.client.Element gwtUserElement = event
                        .getNativeEvent().getEventTarget().cast();

                Widget widget = findWidget(gwtUserElement, classesAllowed);
                // System.out.println(gwtUserElement);

                if (widget != null) {

                    addToMoveAround(widget, (mousex - lastX), (mousey - lastY),
                            mousex, mousey);
                } else {
                }

                lastX = mousex;
                lastY = mousey;

            }

        }, MouseMoveEvent.getType());

    }

    @SuppressWarnings("unchecked")
    public static <T> T findWidget(Element element,
            ArrayList<Class<? extends Widget>> classes) {
        if (element != null) {
            /* First seek for the first EventListener (~Widget) from dom */
            EventListener eventListener = null;
            while (eventListener == null && element != null) {
                eventListener = Event.getEventListener(element);
                if (eventListener == null) {
                    element = (Element) element.getParentElement();
                }
            }
            if (eventListener != null) {

                Widget w = (Widget) eventListener;
                while (w != null) {
                    for (Class<? extends Widget> class1 : classes) {
                        if (class1 == null || w.getClass() == class1) {
                            return (T) w;
                        }
                    }
                    w = w.getParent();
                }
            }
        }
        return null;
    }

}
