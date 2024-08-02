package online.manongbbq.aieducation.ai;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>Activity一定要实现{@link ImageToText.OnTextResultListener}接口</p>
 * <p><strong>eg:</strong></p>
 * <pre>
 * public class MainActivity extends AppCompatActivity implements ImageToText.OnTextResultListener {
 *
 *     private static final String TAG = "MainActivity";
 *     private ImageToText imageToText;
 *
 *     &#064;Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         // 初始化 ImageToText 实例
 *         imageToText = new ImageToText();
 *
 *         // 假设你有一张图片文件
 *         File imageFile = new File("/path/to/your/image.jpg");
 *
 *         // 调用 toText 方法进行 OCR
 *         imageToText.toText(imageFile, this);
 *     }
 *
 *     &#064;Override
 *     public void onResult(String result) {
 *         // 在这里处理识别结果
 *         Log.d(TAG, "识别结果: " + result);
 *         Toast.makeText(this, "识别结果: " + result, Toast.LENGTH_LONG).show();
 *
 *         // 可以在这里更新UI或者处理其他逻辑
 *     }
 * }
 * </pre>
 */
public class ImgToText {

    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e";
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw";
    private static final String APP_ID = "c72c8624";
    private static final String API_URL = "https://api.xf-yun.com/v1/private/sf8e6aca1";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    // 接收一个图片文件，执行OCR并返回结果
    public void toText(File imageFile, OnTextResultListener listener) {
        executorService.execute(() -> {
            try {
                // 1. 读取图片文件并进行Base64编码
                String base64Image = encodeImageToBase64(imageFile);

                // 2. 构造请求参数
                String requestBody = buildRequestBody(base64Image);

                // 3. 发送HTTP请求并获取响应
                String response = sendPostRequest(API_URL, requestBody);

                // 4. 处理响应结果
                String result = parseResponse(response);

                // 在主线程中回调结果
                mainThreadHandler.post(() -> {
                    if (listener != null) {
                        listener.onResult(result);
                    }
                });
            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                mainThreadHandler.post(() -> {
                    if (listener != null) {
                        listener.onResult("Error: " + e.getMessage());
                    }
                });
            }
        });
    }

    // 将图片文件编码为Base64字符串
    private String encodeImageToBase64(File imageFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] bytes = new byte[(int) imageFile.length()];
        inputStream.read(bytes);
        inputStream.close();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    // 构造请求参数
    private String buildRequestBody(String base64Image) {
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
        return Base64.encodeToString(authorizationOrigin.getBytes(), Base64.NO_WRAP);
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
        return Base64.encodeToString(signatureBytes, Base64.NO_WRAP);
    }

    // 获取当前时间的RFC1123格式
    private String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    // 解析响应结果
    private String parseResponse(String response) {
        try {
            //  response 是 JSON 格式的字符串
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject payload = jsonResponse.getJSONObject("payload");
            JSONObject result = payload.getJSONObject("result");
            String text = result.getString("text");

            // 对 text 字段进行 Base64 解码
            byte[] decodedBytes = Base64.decode(text, Base64.NO_WRAP);
            String decodedText = new String(decodedBytes);

            // 解析解码后的 JSON 字符串
            JSONObject decodedJson = new JSONObject(decodedText);
            JSONArray pages = decodedJson.getJSONArray("pages");

            StringBuilder resultBuilder = new StringBuilder();

            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = pages.getJSONObject(i);
                JSONArray lines = page.getJSONArray("lines");

                for (int j = 0; j < lines.length(); j++) {
                    JSONObject line = lines.getJSONObject(j);
                    JSONArray words = line.getJSONArray("words");

                    for (int k = 0; k < words.length(); k++) {
                        JSONObject word = words.getJSONObject(k);
                        String content = word.getString("content");
                        float conf = (float) word.getDouble("conf");

                        Log.d("识别置信度", String.format("单词: %s, 置信度: %.2f\n", content, conf));
                    }
                }
            }

            return resultBuilder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing response: " + e.getMessage();
        }
    }

    // 定义结果回调接口
    public interface OnTextResultListener {
        void onResult(String result);
    }
}
