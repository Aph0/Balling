package com.example.balling;

import java.lang.reflect.Method;

import com.vaadin.util.ReflectTools;

public interface BallerRecordListener {

    public static final Method NEW_RECORD = ReflectTools.findMethod(
            BallerRecordListener.class, "onNewRecord", BallerRecordEvent.class);

    void onNewRecord(BallerRecordEvent event);

}
