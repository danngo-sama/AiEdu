package online.manongbbq.aieducation.ai;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import okhttp3.*;
//import online.manongbbq.aieducation.BigModelNew.BigModelNew;
//
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//public class RobotAssistant {
//    private static final String API_URL = "https://spark-api.xf-yun.com/v3.5/chat";
//    private static final String APP_ID = "c72c8624"; // appid
//    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw"; // apiSecret
//    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e"; // apiKey
//
//    public String getAnswer(String question) {
//        final CountDownLatch latch = new CountDownLatch(1);
//        final String[] answer = {""};
//
//        try {
//            String authUrl = BigModelNew.getAuthUrl(API_URL, API_KEY, API_SECRET);
//            OkHttpClient client = new OkHttpClient.Builder().build();
//            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
//            Request request = new Request.Builder().url(url).build();
//            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
//                @Override
//                public void onOpen(WebSocket webSocket, Response response) {
//                    JSONObject requestJson = new JSONObject();
//
//                    JSONObject header = new JSONObject();
//                    header.put("app_id", APP_ID);
//                    header.put("uid", UUID.randomUUID().toString().substring(0, 10));
//
//                    JSONObject parameter = new JSONObject();
//                    JSONObject chat = new JSONObject();
//                    chat.put("domain", "generalv3.5");
//                    chat.put("temperature", 0.5);
//                    chat.put("max_tokens", 4096);
//                    parameter.put("chat", chat);
//
//                    JSONObject payload = new JSONObject();
//                    JSONObject message = new JSONObject();
//                    JSONObject textObj = new JSONObject();
//                    textObj.put("role", "user");
//                    textObj.put("content", question);
//
//                    message.put("text", textObj);
//                    payload.put("message", message);
//
//                    requestJson.put("header", header);
//                    requestJson.put("parameter", parameter);
//                    requestJson.put("payload", payload);
//
//                    webSocket.send(requestJson.toString());
//                }
//
//                @Override
//                public void onMessage(WebSocket webSocket, String text) {
//                    JSONObject jsonResponse = JSON.parseObject(text);
//                    List<JSONObject> textList = jsonResponse.getJSONObject("payload").getJSONObject("choices").getJSONArray("text").toJavaList(JSONObject.class);
//                    StringBuilder totalAnswer = new StringBuilder();
//                    for (JSONObject temp : textList) {
//                        totalAnswer.append(temp.getString("content"));
//                    }
//
//                    answer[0] = totalAnswer.toString();
//
//                    if (jsonResponse.getJSONObject("header").getInteger("status") == 2) {
//                        webSocket.close(1000, "");
//                        latch.countDown();
//                    }
//                }
//
//                @Override
//                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//                    t.printStackTrace();
//                    latch.countDown();
//                }
//            });
//
//            latch.await(30, TimeUnit.SECONDS); // 等待响应最多30秒
//            return answer[0]; // 返回回答结果
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
import android.content.Context;
import android.util.Log;

import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMOutput;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;


public class RobotAssistant {

    private static final String TAG = "RobotAssistant";
    private static final String APP_ID = "c72c8624"; // 替换为实际的appId
    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e"; // 替换为实际的apiKey
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw"; // 替换为实际的apiSecret

    private LLM llm;
    private Context context;

    public RobotAssistant(Context context) {
        this.context = context;
        initializeSdk();
    }

    private void initializeSdk() {
        SparkChainConfig config = SparkChainConfig.builder()
                .appID(APP_ID)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                /*.build()*/;

        int ret = SparkChain.getInst().init(context, config);
        Log.d(TAG, "SDK init: " + ret);

        LLMConfig llmConfig = LLMConfig.builder()
                .domain("generalv3.5")
                .url("wss://spark-api.xf-yun.com/v3.5/chat")
                /*.build()*/;

        llm = new LLM(llmConfig);
    }

    public String getAnswer(String question) {
        if (llm == null) {
            Log.e(TAG, "LLM not initialized.");
            return "Initialization error";
        }

        LLMOutput output = llm.run(question);
        if (output.getErrCode() == 0) {
            Log.i(TAG, "Sync call: " + output.getRole() + ": " + output.getContent());
            return output.getContent();
        } else {
            Log.e(TAG, "Sync call error: " + output.getErrCode() + " " + output.getErrMsg());
            return "Error: " + output.getErrMsg();
        }
    }
}