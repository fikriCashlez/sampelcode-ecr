package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static String URL = "viper.cashlez.com/"; //TODO: BASE URL

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        doPaymentRequestECR();
    }

    private static void doPaymentRequestECR() {
        String endPoint = "https://" + URL + "/MmBridgeApi/v1/payment-request";
        String clientId = "CLID-9512DD2103143019";
        String deviceUser = "czpartnership3"; //TODO Please Change with your userName
        String callbackURL = "https://webhook.site/bd2a7577-7641-4e90-9e11-c1759c04b3a7"; //TODO please change with your callbackURL
        long timestamp = getCurrentTimestamp();
        String requestId = String.format("ReqId-%d", timestamp); //TODO Format -> ReqId+timestamp
        String postTypeRequest = String.format("sampelcodeECR-%d", timestamp); //TODO Format -> nameMerchant+timestamp
        String amount = "1112"; //TODO change your amount;

        OkHttpClient client = new OkHttpClient();
        // CREATE JSON body
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("pos_request_type", postTypeRequest);
        jsonBody.put("request_id", requestId);
        jsonBody.put("client_id", clientId);
        jsonBody.put("device_user", deviceUser);
        jsonBody.put("payment_method", "CDCP");
        jsonBody.put("amount", amount);
        jsonBody.put("callback_url", callbackURL);

        String json = new Gson().toJson(jsonBody);

        RequestBody requestBody = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(endPoint)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("------------------------Data Plain Payment Request-------------------------------------");
            System.out.println("deviceUser---------------------: " + deviceUser);
            System.out.println("client_id-------------: " + clientId);
            System.out.println("callback_url--------------------: " + callbackURL);
            System.out.println("-------------------------Data Plain Payment Process-----------------------------------------------");
            System.out.println("URL path PaymentRequest-----------:" + request.url().url().getPath());
            System.out.println("Request body-------------:\n" + json);

            String responseBody = response.body().string();
            System.out.println("Response body------------:\n" + responseBody);

            // Parsing JSON untuk ambil token
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.get("ws_token").getAsString() != null) {
                doPaymentSubscribe(jsonObject.get("ws_token").getAsString(), requestId);
                System.out.println("✅WS Token------------: " + jsonObject.get("ws_token").getAsString());
            } else {
                System.out.println("❌Failed------------: " + responseBody);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static void doPaymentSubscribe(String wsToken, String requestId) {
        try {
            URI uri = new URI("wss://" + URL + "MmBridgeApi/v1/ws/payment-subscribe");

            //TODO add Header
            Map<String, String> headers = new HashMap<>();
            headers.put("token", wsToken);

            MyWebSocketClient client = new MyWebSocketClient(uri, headers, requestId);
            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}