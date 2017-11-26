package com.tikalk.antspublisher.core.model;

public class LocationReport extends EventBusMessage{
    public String id = "";
    public String species = "";
    public int xPromil = 0;
    public int yPromil = 0;

    public LocationReport(){

    }

    public String asEventBusMessage(){
        return toString();
    }
}
