package com.example.balling.client.baller;

import java.util.ArrayList;
import java.util.List;

public class BallerState extends com.vaadin.shared.AbstractComponentState {

    public List<AllowedWidgets> allowedWidgets = new ArrayList<AllowedWidgets>();
    public boolean hitHandlerEnabled;
    public boolean recordHandlerEnabled;
    public boolean isWicked = true;
}