package com.example.balling;

import java.lang.reflect.Method;

import com.vaadin.util.ReflectTools;

public interface BallerTouchListener {

    public static final Method OBJECT_TOUCHED = ReflectTools.findMethod(
            BallerTouchListener.class, "objectTouched", BallerTouchEvent.class);

    void objectTouched(BallerTouchEvent event);

}
