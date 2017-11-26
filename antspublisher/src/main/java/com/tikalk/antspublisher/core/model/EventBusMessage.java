package com.tikalk.antspublisher.core.model;

import io.vertx.core.json.Json;

public class EventBusMessage {

    @Override
    public String toString() {
        return Json.encode(this);
    }
}
