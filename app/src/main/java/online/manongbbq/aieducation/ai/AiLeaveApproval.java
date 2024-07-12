package online.manongbbq.aieducation.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import online.manongbbq.aieducation.BigModelNew.BigModelNew;

public class AiLeaveApproval {
    private static final String API_URL = "https://spark-api.xf-yun.com/v2.1/chat";
    private static final String APP_ID = "c72c8624"; // appid
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw"; // apiSecret
    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e"; // apiKey

    public boolean getApproval(String askForLeave) {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        try {
            String authUrl = BigModelNew.getAuthUrl(API_URL, API_KEY, API_SECRET);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    JSONObject requestJson = new JSONObject();

                    JSONObject header = new JSONObject();
                    header.put("app_id", APP_ID);
                    header.put("uid", UUID.randomUUID().toString().substring(0, 10));

                    JSONObject parameter = new JSONObject();
                    JSONObject chat = new JSONObject();
                    chat.put("domain", "generalv2");
                    chat.put("temperature", 0.5);
                    chat.put("max_tokens", 4096);
                    parameter.put("chat", chat);

                    JSONObject payload = new JSONObject();
                    JSONObject message = new JSONObject();
                    JSONObject text = new JSONObject();
                    text.put("role", "user");
                    text.put("content", askForLeave);

                    message.put("text", text);
                    payload.put("message", message);

                    requestJson.put("header", header);
                    requestJson.put("parameter", parameter);
                    requestJson.put("payload", payload);

                    webSocket.send(requestJson.toString());
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    JSONObject jsonResponse = JSON.parseObject(text);
                    List<JSONObject> textList = jsonResponse.getJSONObject("payload").getJSONObject("choices").getJSONArray("text").toJavaList(JSONObject.class);
                    StringBuilder totalAnswer = new StringBuilder();
                    for (JSONObject temp : textList) {
                        totalAnswer.append(temp.getString("content"));
                    }

                    if (jsonResponse.getJSONObject("header").getInteger("status") == 2) {
                        webSocket.close(1000, "");
                        // 根据回答内容判断是否批准
                        if (totalAnswer.toString().contains("批准")) {
                            resultFuture.complete(true);
                        } else {
                            resultFuture.complete(false);
                        }
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    t.printStackTrace();
                    resultFuture.completeExceptionally(t);
                }
            });

            return resultFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleApproval(String askForLeave) {
        boolean isApproved = getApproval(askForLeave);
        if (isApproved) {
            // 处理批准逻辑
            System.out.println("请假请求已批准");
        } else {
            // 将请假信息发送到教师客户端
            System.out.println("请假请求未批准，已发送到教师客户端");
        }
    }

    public static void main(String[] args) {
        AiLeaveApproval approval = new AiLeaveApproval();
        String askForLeave = "学生张三请求请假一天，原因是生病。";
        approval.handleApproval(askForLeave);
    }
}
