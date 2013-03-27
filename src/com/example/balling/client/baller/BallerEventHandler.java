package com.example.balling.client.baller;

import com.google.gwt.event.shared.EventHandler;

public interface BallerEventHandler extends EventHandler {
    void onObjectHit(int x, int y, int hits);

    void onHitRecord(int hits);
}
