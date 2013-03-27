package com.example.balling;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

public class BallerRecordEvent extends Event {

    private final int record;

    public BallerRecordEvent(Component source, int record) {
        super(source);
        this.record = record;
    }

    public int getRecord() {
        return record;
    }

}
