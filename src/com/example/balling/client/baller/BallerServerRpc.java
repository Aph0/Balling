package com.example.balling.client.baller;

import com.vaadin.shared.communication.ServerRpc;

public interface BallerServerRpc extends ServerRpc {

    public void objectHit(int x, int y, int hits);

    public void hitRecord(int hits);

}
