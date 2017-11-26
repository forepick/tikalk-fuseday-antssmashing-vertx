package com.tikalk.antspublisher.core.model;

import io.vertx.core.impl.StringEscapeUtils;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class SockJSMessage extends EventBusMessage{

    public String type = "";
    public String address = "";
    public Map<String, String> headers = new HashMap();
    public LocationReport body = null;

    public SockJSMessage(){

    }

    public String asEventBusMessage(){
        try {
            return "[\"" + StringEscapeUtils.escapeJava(toString()) + "\"]";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
