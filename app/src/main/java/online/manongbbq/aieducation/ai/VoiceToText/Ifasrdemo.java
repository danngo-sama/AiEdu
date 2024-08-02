package online.manongbbq.aieducation.ai.VoiceToText;

import android.content.Context;
import android.widget.Toast;

import cn.hutool.json.JSONUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import online.manongbbq.aieducation.activity.VoiceToTextActivity;
import online.manongbbq.aieducation.ai.VoiceToText.sign.LfasrSignature;
import online.manongbbq.aieducation.ai.VoiceToText.utils.HttpUtil;
import com.google.gson.Gson;

import java.io.*;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ifasrdemo {
    private static final String HOST = "https://raasr.xfyun.cn";
    private static final String APP_ID = "7178fd9a";
    private static final String KEY_SECRET = "ccd4ffaf7285e9f2f907b47f1010e7e5";
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = Logger.getLogger(Ifasrdemo.class.getName());

    /**
     * 上传音频文件到服务
     *
     * @param audioFilePath 音频文件的路径
     * @return 上传结果
     * @throws SignatureException 签名异常
     * @throws FileNotFoundException 文件未找到异常
     */
    public static String upload(VoiceToTextActivity activity, String audioFilePath) throws SignatureException, IOException {
        File audio = new File(audioFilePath);

        if (!audio.exists() || !audio.canRead()) {
//            activity.appendDebugLog("音频文件未找到或无法读取：" + audioFilePath);
            throw new FileNotFoundException("音频文件未找到或无法读取：" + audioFilePath);
        }

//        activity.appendDebugLog("音频文件准备上传，路径: " + audioFilePath);

        String fileName = audio.getName();
        long fileSize = audio.length();

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("appId", APP_ID);
        map.put("fileSize", fileSize);
        map.put("fileName", fileName);
        map.put("duration", "200");
        map.put("resultType", "transfer");

//        activity.appendDebugLog("请求参数: appId=" + APP_ID + ", fileSize=" + fileSize + ", fileName=" + fileName);

        LfasrSignature lfasrSignature = new LfasrSignature(APP_ID, KEY_SECRET);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());

        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = "https://raasr.xfyun.cn/v2/api/upload" + "?" + paramString;

//        activity.appendDebugLog("请求URL: " + url);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new InputStreamRequestBody(new FileInputStream(audio));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/octet-stream")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = "HTTP请求失败，状态码: " + response.code() + ", 响应消息: " + response.message();
//                activity.appendDebugLog(errorMessage);
                throw new IOException(errorMessage);
            }

            String responseBody = response.body().string();
            if (responseBody == null || responseBody.isEmpty()) {
//                activity.appendDebugLog("上传失败，服务器未返回响应");
                throw new IOException("上传失败，服务器未返回响应");
            }
//
//            activity.appendDebugLog("服务器响应: " + responseBody);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "上传音频文件时发生IO错误: " + e.getMessage();
//            activity.appendDebugLog(errorMessage);
            throw new RuntimeException("音频上传失败", e);
        }
    }






    // 自定义 RequestBody 用于处理 InputStream



    /**
     * 获取语音识别结果
     *
     * @param orderId 订单ID
     * @return 识别结果
     * @throws SignatureException 签名异常
     * @throws InterruptedException 线程中断异常
     * @throws IOException IO异常
     */
    public static String getResult(VoiceToTextActivity activity, String orderId) throws SignatureException, InterruptedException, IOException {
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("orderId", orderId);
        LfasrSignature lfasrSignature = new LfasrSignature(APP_ID, KEY_SECRET);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        map.put("appId", APP_ID);
        map.put("resultType", "transfer");

        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = HOST + "/v2/api/getResult" + "?" + paramString;
//        activity.appendDebugLog("get_result_url: " + url);

        while (true) {
            String response = HttpUtil.iflyrecGet(url);
            if (response == null || response.isEmpty()) {
//                activity.appendDebugLog("获取识别结果失败，服务器未返回响应");
                throw new IOException("获取识别结果失败，服务器未返回响应");
            }

//            activity.appendDebugLog("服务器响应: " + response);

            JsonParse jsonParse = GSON.fromJson(response, JsonParse.class);
            int status = jsonParse.content.orderInfo.status;

//            activity.appendDebugLog("步骤8");

            if (status == 4) {
//                activity.appendDebugLog("订单完成: " + response);

                return response;
            } else if (status == -1) {
//                activity.appendDebugLog("订单失败: " + response);
                throw new RuntimeException("订单处理失败，服务器响应: " + response);
            } else {
//                activity.appendDebugLog("进行中... 状态为: " + status);
                Thread.sleep(7000);  // 每隔7秒检查一次状态
            }
        }
    }



//    /**
//     * 将结果写入文件
//     *
//     * @param response 识别结果的响应
//     * @throws IOException IO异常
//     */
//    public static void write(String response) throws IOException {
//        File outputDir = new File("src/main/resources/output");
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputDir, "test.txt")))) {
//            bw.write(response);
//            LOGGER.info("结果写入成功");
//        } catch (IOException e) {
//            LOGGER.log(Level.SEVERE, "写入文件时发生错误", e);
//            throw e;
//        }
//    }

    private static class JsonParse {
        Content content;
    }

    private static class Content {
        OrderInfo orderInfo;
    }

    private static class OrderInfo {
        Integer status;
    }
}
