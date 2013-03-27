package com.example.balling;

import java.util.List;

import com.example.balling.client.baller.AllowedWidgets;
import com.example.balling.client.baller.BallerServerRpc;
import com.example.balling.client.baller.BallerState;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

public class Baller extends AbstractExtension {

    public Baller() {
        registerRpc(rpc);
    }

    private BallerServerRpc rpc = new BallerServerRpc() {

        @Override
        public void objectHit(int x, int y, int hits) {
            fireEvent(new BallerTouchEvent(component, x, y, hits));

        }

        @Override
        public void hitRecord(int hits) {
            fireEvent(new BallerRecordEvent(component, hits));

        }

    };
    private AbstractComponent component;

    @Override
    public BallerState getState() {
        return (BallerState) super.getState();
    }

    public void addWickedWidgetType(AllowedWidgets wickedWidget) {
        getState().allowedWidgets.add(wickedWidget);
        markAsDirty();
    }

    public void setWickedWidgetTypes(List<AllowedWidgets> widgets) {
        getState().allowedWidgets = widgets;
        markAsDirty();
    }

    public void allowBallerHitEvents(boolean enabled) {
        getState().hitHandlerEnabled = enabled;
    }

    public void allowBallerRecordEvents(boolean enabled) {
        getState().recordHandlerEnabled = enabled;
    }

    public void addBallerTouchListener(BallerTouchListener listener) {
        addListener(BallerTouchEvent.class, listener,
                BallerTouchListener.OBJECT_TOUCHED);
    }

    public void removeBallerTouchListener(BallerTouchListener listener) {
        addListener(BallerTouchEvent.class, listener,
                BallerTouchListener.OBJECT_TOUCHED);
    }

    public void addBallerRecordListener(BallerRecordListener listener) {
        addListener(BallerRecordEvent.class, listener,
                BallerRecordListener.NEW_RECORD);
    }

    public void removeBallerRecordListener(BallerRecordListener listener) {
        addListener(BallerTouchEvent.class, listener,
                BallerRecordListener.NEW_RECORD);
    }

    public void setWicked(boolean wicked) {
        getState().isWicked = wicked;
    }

    public boolean isWicked() {
        return getState().isWicked;
    }

    public void extendComponent(AbstractComponent c) {
        component = c;
        extend(c);
        markAsDirty();
    }
}
