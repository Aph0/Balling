package com.example.balling;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

public class BallerTouchEvent extends Event {

    private final int x;
    private final int y;
    private final int touches;

    public BallerTouchEvent(Component source, int x, int y, int touches) {
        super(source);
        this.x = x;
        this.y = y;
        this.touches = touches;
    }

    public int getTouches() {
        return touches;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

}
