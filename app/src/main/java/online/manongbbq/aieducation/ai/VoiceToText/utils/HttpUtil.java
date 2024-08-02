package online.manongbbq.aieducation.ai.VoiceToText.utils;

import okhttp3.*;
import okio.BufferedSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * HTTP请求工具类
 */
public class HttpUtil {
    private HttpUtil() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final String UTF8 = "UTF-8";
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 请求的upload接口, 发送音频创建转写订单
     *
     * @param url 请求地址
     * @param in  需要转写的音频流
     * @return 返回结果
     */
    public static String iflyrecUpload(String url, InputStream in) {
        try {
            // 将 InputStream 转换为 RequestBody
            RequestBody requestBody = new InputStreamRequestBody(in);

            // 创建 POST 请求
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            // 执行请求
            return executeRequest(request);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
            return null;
        }
    }

    /**
     * 请求听见的获取结果接口
     *
     * @param url 请求路径
     * @return 返回结果
     */
    public static String iflyrecGet(String url) {
        try {
            // 创建 GET 请求
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            // 执行请求
            return executeRequest(request);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
            return null;
        }
    }

    /**
     * 流控组件调用
     *
     * @param url 请求路径
     * @return 返回结果
     */
    public static String flowCtrlGet(String url) {
        try {
            // 创建 GET 请求
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            // 执行请求
            return executeRequest(request);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
            return null;
        }
    }

    /**
     * 流传输的post
     *
     * @param url  请求路径
     * @param body 字节流数据
     * @return 返回结果
     */
    public static String post(String url, byte[] body) {
        try {
            // 创建 POST 请求
            RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/octet-stream"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            // 执行请求
            return executeRequest(request);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
            return null;
        }
    }

    /**
     * 带字符串参数的post请求
     *
     * @param url   请求路径
     * @param param 字符串参数
     * @return 返回结果
     */
    public static String post(String url, String param) {
        try {
            // 创建 POST 请求
            RequestBody requestBody = RequestBody.create(param, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            // 执行请求
            return executeRequest(request);
        } catch (Exception e) {
            LOGGER.error("网络异常", e);
            return null;
        }
    }

    /**
     * 发送HttpGet请求
     *
     * @param url 请求路径
     * @return 返回结果
     */
    public static String sendGet(String url) {
        return iflyrecGet(url);
    }

    /**
     * 执行网络请求
     *
     * @param request http请求对象
     * @return 返回结果
     */
    private static String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.error("网络异常: HTTP " + response.code());
                return null;
            }
            return response.body().string();
        }
    }

    /**
     * 发送post请求
     *
     * @param url    请求路径
     * @param header 请求头
     * @param body   请求数据
     * @return 返回结果
     */
    public static String postWithHeader(String url, Map<String, String> header, String body) {
        try {
            // 创建 POST 请求
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(body, MediaType.parse("application/json")));

            // 设置请求头
            for (Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }

            // 执行请求
            return executeRequest(builder.build());
        } catch (Exception e) {
            LOGGER.error("HttpUtil postWithHeader Exception!", e);
            return null;
        }
    }

    /**
     * 将集合转换为路径参数
     *
     * @param param 集合参数
     * @return 路径参数
     */
    public static String parseMapToPathParam(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        try {
            Set<Entry<String, Object>> entryset = param.entrySet();
            boolean isFirst = true;
            for (Entry<String, Object> entry : entryset) {
                if (!isFirst) {
                    sb.append("&");
                } else {
                    isFirst = false;
                }
                sb.append(URLEncoder.encode(entry.getKey(), UTF8));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("HttpUtil parseMapToPathParam Exception!", e);
        }

        return sb.toString();
    }

    // 自定义 RequestBody 用于处理 InputStream
    private static class InputStreamRequestBody extends RequestBody {
        private final InputStream inputStream;

        public InputStreamRequestBody(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                sink.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        }
    }
}
