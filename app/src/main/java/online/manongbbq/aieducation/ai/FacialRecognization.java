package online.manongbbq.aieducation.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.*;

import online.manongbbq.aieducation.BigModelNew.BigModelNew;

public class FacialRecognization {
    private static final String API_URL = "https://api.xf-yun.com/v1/private/s67c9c78c";
    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e";
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw";

    public boolean getResult(String imageBase64_1,  String imageBase64_2){
        try {
            String host = "api.xf-yun.com";
            String date = getServeTime();
            String requestLine = "POST /v1/private/s67c9c78c HTTP/1.1";

            String signatureOrigin = "host: " + host + "\ndate: " + date + "\n" + requestLine;
            String signature = generateHMAC(signatureOrigin, API_SECRET);
            String authorizationOrigin = "api_key=\"" + API_KEY + "\", alogrithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signature + "\"";
            String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes());

            //HttpClient client = HttpClient.newHttpClient();
            OkHttpClient client = new OkHttpClient();
            String requestBody = generateRequestBody(imageBase64_1, imageBase64_2);

//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(API_URL + "?authorization=" + authorization))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .build();
            Request request = new Request.Builder()
                    .url(API_URL + "?authorization=" + authorization)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            //HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Response response = client.newCall(request).execute();

            //return parseResponse(response.body());
            return parseResponse(response.body().string());
        }catch (Exception e){
            e.printStackTrace();;
            return false;
        }
    }

    private String getServeTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    private String generateHMAC(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    private String generateRequestBody(String imageBase64_1, String imageBase64_2){
        return "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"your_app_id\",\n" +
                "    \"status\": 3\n" +
                "  },\n" +
                "  \"parameter\": {\n" +
                "    \"s67c9c78c\": {\n" +
                "      \"service_kind\": \"face_compare\",\n" +
                "      \"face_compare_result\": {\n" +
                "        \"encoding\": \"utf8\",\n" +
                "        \"compress\": \"raw\",\n" +
                "        \"format\": \"json\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"payload\": {\n" +
                "    \"input1\": {\n" +
                "      \"encoding\": \"jpg\",\n" +
                "      \"status\": 3,\n" +
                "      \"image\": \"" + imageBase64_1 + "\"\n" +
                "    },\n" +
                "    \"input2\": {\n" +
                "      \"encoding\": \"jpg\",\n" +
                "      \"status\": 3,\n" +
                "      \"image\": \"" + imageBase64_2 + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private boolean parseResponse(String responseBody){
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        String encoderText = jsonObject.get("payload").getAsJsonObject()
                .get("face_compare_result").getAsJsonObject()
                .get("text").getAsString();
        String decodedText = new String(Base64.getDecoder().decode(encoderText));
        JsonObject result = JsonParser.parseString(decodedText).getAsJsonObject();
        float score = result.get("score").getAsFloat();
        return score > 0.67;
    }
}
