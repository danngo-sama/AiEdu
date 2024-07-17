package online.manongbbq.aieducation.ai;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ImageToText {

    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e";
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw";
    private static final String APP_ID = "c72c8624";
    private static final String API_URL = "https://api.xf-yun/v1/private/sf8e6aca1";

    public void toText(String imagePath, OnTextResultListener listener) {
        new ImageToTextTask(listener).execute(imagePath);
    }

    // 异步任务类
    private class ImageToTextTask extends AsyncTask<String, Void, String> {
        private OnTextResultListener listener;

        public ImageToTextTask(OnTextResultListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            String imagePath = params[0];
            try {
                // 1. 读取图片文件并进行Base64编码
                File imageFile = new File(imagePath);
                String base64Image = encodeImageToBase64(imageFile);

                // 2. 构造请求参数
                String requestBody = buildRequestBody(base64Image);

                // 3. 发送HTTP请求并获取响应
                String response = sendPostRequest(API_URL, requestBody);

                // 4. 处理响应结果
                return parseResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                listener.onResult(result);
            }
        }
    }

    // 将图片文件编码为Base64字符串
    private String encodeImageToBase64(File imageFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] bytes = new byte[(int) imageFile.length()];
        inputStream.read(bytes);
        inputStream.close();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // 构造请求参数
    private String buildRequestBody(String base64Image) throws NoSuchAlgorithmException, InvalidKeyException {
        String date = getFormattedDate();
        String signature = generateSignature(date);
        return "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"" + APP_ID + "\",\n" +
                "    \"status\": 3\n" +
                "  },\n" +
                "  \"parameter\": {\n" +
                "    \"sf8e6aca1\": {\n" +
                "      \"category\": \"ch_en_public_cloud\",\n" +
                "      \"result\": {\n" +
                "        \"encoding\": \"utf8\",\n" +
                "        \"compress\": \"raw\",\n" +
                "        \"format\": \"json\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"payload\": {\n" +
                "    \"sf8e6aca1_data_1\": {\n" +
                "      \"encoding\": \"jpg\",\n" +
                "      \"status\": 3,\n" +
                "      \"image\": \"" + base64Image + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    // 获取当前时间的RFC1123格式
    private String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    // 生成签名
    private String generateSignature(String date) throws NoSuchAlgorithmException, InvalidKeyException {
        String signatureOrigin = "host: api.xf-yun.com\n" +
                "date: " + date + "\n" +
                "POST /v1/private/sf8e6aca1 HTTP/1.1";
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] signatureBytes = sha256Hmac.doFinal(signatureOrigin.getBytes());
        return Base64.encodeToString(signatureBytes, Base64.DEFAULT);
    }

    // 发送POST请求
    private String sendPostRequest(String apiUrl, String requestBody) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", getAuthorizationHeader());
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        }

        try (BufferedReader responseReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    // 获取Authorization头部信息
    private String getAuthorizationHeader() throws NoSuchAlgorithmException, InvalidKeyException {
        String date = getFormattedDate();
        String signature = generateSignature(date);
        String authorizationOrigin = "api_key=\"" + API_KEY + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signature + "\"";
        return "YXBpX2tleT0i" + Base64.encodeToString(authorizationOrigin.getBytes(), Base64.DEFAULT);
    }

    // 解析响应结果
    private String parseResponse(String response) {
        // 解析JSON响应，获取文本信息
        // 根据返回的JSON格式，具体解析逻辑需要根据实际情况编写
        return "Parsed text from response";
    }

    // 定义结果回调接口
    public interface OnTextResultListener {
        void onResult(String result);
    }
}