package online.manongbbq.aieducation.ai;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import java.util.Base64;
import java.text.SimpleDateFormat;


public class ImageToText {

    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e";
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw";
    private static final String API_HOST = "api.xf-yun.com";
    private static final String API_ENDPOINT = "/v1/private/sf8e6aca1";

    public String toText(String imageBase64) throws Exception {
        // 构建请求参数
        String date = getRFC1123Date(); // 获取当前时间，格式化成 RFC1123 格式
        String requestLine = "POST " + API_ENDPOINT + " HTTP/1.1";
        String signatureOrigin = "host: " + API_HOST + "\ndate: " + date + "\n" + requestLine;
        String signatureSha = signWithHmacSHA256(signatureOrigin, API_SECRET);
        String authorizationOrigin = "api_key=\"" + API_KEY + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signatureSha + "\"";
        String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes("UTF-8"));

        // 构建 HTTP 请求
        URL url = new URL("https://" + API_HOST + API_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authorization);
        conn.setDoOutput(true);

        // 构建请求体
        String requestBody = buildRequestBody(imageBase64, date);
        OutputStream os = conn.getOutputStream();
        os.write(requestBody.getBytes("UTF-8"));
        os.flush();

        // 处理响应
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("Request failed with HTTP error code: " + responseCode);
        }
    }

    private String buildRequestBody(String imageBase64, String date) {
        String jsonBody = "{" +
                "\"header\": {" +
                "\"app_id\": \"your_app_id\"," +
                "\"status\": 3" +
                "}," +
                "\"parameter\": {" +
                "\"sf8e6aca1\": {" +
                "\"category\": \"ch_en_public_cloud\"," +
                "\"result\": {" +
                "\"encoding\": \"utf8\"," +
                "\"compress\": \"raw\"," +
                "\"format\": \"json\"" +
                "}" +
                "}" +
                "}," +
                "\"payload\": {" +
                "\"sf8e6aca1_data_1\": {" +
                "\"encoding\": \"jpg\"," +
                "\"status\": 3," +
                "\"image\": \"" + imageBase64 + "\"" +
                "}" +
                "}" +
                "}";
        return jsonBody;
    }

    private String signWithHmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    private String getRFC1123Date() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(now);
    }
}
