package com.tikalk.antspublisher.core;


import com.tikalk.antspublisher.Constants;
import com.tikalk.antspublisher.core.model.SockJSMessage;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.TimeUnit;

public class TestAntsClient extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10000,  TimeUnit.MILLISECONDS)
                .build();
        String server = "localhost";
//        String server = "52.41.200.225";
        Request request = new Request.Builder()
                .url("ws://" + server+":" + Constants.DEFAULT_PORT + "/client.register/123/abc_153665080266086/websocket")
                .build();


        client.newWebSocket(request, this);
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }


    @Override public void onOpen(WebSocket webSocket, Response response) {
        new Thread(()->ping(webSocket)).start();

        SockJSMessage message = new SockJSMessage();
        message.type = "register";
        message.address = Constants.LOCATION_EVENTBUS_ADDRESS;
        webSocket.send(message.asEventBusMessage());



        //webSocket.send("[\"{\\\"type\\\":\\\"register\\\",\\\"address\\\":\\\"game-state-address\\\",\\\"headers\\\":{}}\"]");
    }

    private void ping(WebSocket webSocket) {
        while (true){
            webSocket.send("[\"{\\\"type\\\":\\\"ping\\\"}\"]");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        if(!text.startsWith("h"))
            System.out.println("MESSAGE: " + text);

    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

}
