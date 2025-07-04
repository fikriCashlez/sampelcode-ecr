package org.example;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class MyWebSocketClient extends WebSocketClient {

    private final String requestId;
    private final Gson gson = new Gson();

    public MyWebSocketClient(URI serverUri, Map<String, String> headers, String requestId) {
        super(serverUri, headers);
        this.requestId = requestId;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✅ WebSocket connected");

        // TODO Create Object as JSON
        WsSubscribeRequest request = new WsSubscribeRequest(requestId);
        String json = gson.toJson(request);
        send(json);

        System.out.println("Sent: " + json);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
}
