package com.tikalk.antspublisher.core.model;

import io.vertx.core.impl.StringEscapeUtils;
import io.vertx.core.json.Json;

public class LocationReport extends EventBusMessage{
    public String id = "";
    public String species = "";
    public int xPromil = 0;
    public int yPromil = 0;

    public LocationReport(){

    }

    public String asEventBusMessage(){
        try {
            return "\"" + StringEscapeUtils.escapeJava(toString()) + "\"";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
