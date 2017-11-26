package com.tikalk.antspublisher;

import com.tikalk.antspublisher.core.model.SockJSMessage;
import com.tikalk.antspublisher.web.example.SimpleHttpServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class VariousTest {



    @Test
    public void testMyApplication(TestContext context){

        String res = Json.encode(new TestJson());
        System.out.println(res);

    }

    @Test
    public void TestSockJSMessage(TestContext context){
        SockJSMessage message = new SockJSMessage();
        message.type = "register";
        message.type = Constants.LOCATION_EVENTBUS_ADDRESS;

        System.out.println(message.asEventBusMessage());
    }

    private static class TestJson{

        public String field1 = "1";
        public String field2 = "2";

        public TestJson(){

        }

    }
}
