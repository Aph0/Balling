package com.example.balling;

import com.example.balling.client.baller.AllowedWidgets;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BallingUI extends UI {
    @Override
    public void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        HorizontalLayout hl2 = new HorizontalLayout();
        hl2.setWidth("100%");
        HorizontalLayout hl3 = new HorizontalLayout();
        hl3.setWidth("100%");
        HorizontalLayout hl4 = new HorizontalLayout();
        hl4.setWidth("100%");
        Button b;
        vl.addComponent(hl);
        vl.addComponent(hl2);
        vl.addComponent(hl3);
        vl.addComponent(hl4);
        hl.addComponent(b = new Button("Button"));
        hl.addComponent(new Label("Label"));
        hl.addComponent(new Button("Button"));
        hl2.addComponent(new Label("Label"));
        hl2.addComponent(new TextField("Tfield"));
        hl2.addComponent(new Button("Button long text"));
        hl2.addComponent(new Button("Button"));
        hl2.addComponent(new Button("Button"));
        hl2.addComponent(new Label("Label"));
        hl2.addComponent(new Button("Button"));
        hl2.addComponent(new Button("Button long text Button long text"));
        hl3.addComponent(new Label("Label"));
        hl3.addComponent(new Label("Label"));
        hl4.addComponent(new Button("Button"));
        hl4.addComponent(new TextField());
        hl4.addComponent(new Button("Button"));
        Baller bs = new Baller();
        bs.addWickedWidgetType(AllowedWidgets.BUTTON);
        bs.addWickedWidgetType(AllowedWidgets.TEXTFIELD);
        bs.addWickedWidgetType(AllowedWidgets.CAPTION);
        bs.addWickedWidgetType(AllowedWidgets.LABEL);
        bs.extendComponent(vl);
        setContent(vl);

        bs.addBallerRecordListener(new BallerRecordListener() {

            @Override
            public void onNewRecord(BallerRecordEvent event) {
                System.out.println("Record: " + event.getRecord());

            }
        });

        bs.addBallerTouchListener(new BallerTouchListener() {

            @Override
            public void objectTouched(BallerTouchEvent event) {
                System.out.println("Hits: " + event.getTouches());

            }
        });
        bs.allowBallerHitEvents(true);
        bs.allowBallerRecordEvents(true);
    }
}
