package online.manongbbq.aieducation.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.util.*;
import online.manongbbq.aieducation.BigModelNew.BigModelNew;

public class AiSummary
{
    private static final String API_URL = "https://spark-api.xf-yun.com/v2.1/chat";
    private static final String APP_ID = "c72c8624"; // appid
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw"; // apiSecret
    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e"; // apiKey

    public String getSummary(String text)
    {
        try
        {
            String authUrl = BigModelNew.getAuthUrl(API_URL, API_KEY, API_SECRET);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener()
            {
                @Override
                public void onOpen(WebSocket webSocket, Response response)
                {
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
                    JSONObject textObj = new JSONObject();
                    textObj.put("role", "user");
                    textObj.put("content", text);

                    message.put("text", textObj);
                    payload.put("message", message);

                    requestJson.put("header", header);
                    requestJson.put("parameter", parameter);
                    requestJson.put("payload", payload);

                    webSocket.send(requestJson.toString());
                }

                @Override
                public void onMessage(WebSocket webSocket, String text)
                {
                    JSONObject jsonResponse = JSON.parseObject(text);
                    List<JSONObject> textList = jsonResponse.getJSONObject("payload").getJSONObject("choices").getJSONArray("text").toJavaList(JSONObject.class);
                    StringBuilder totalAnswer = new StringBuilder();
                    for (JSONObject temp : textList)
                    {
                        totalAnswer.append(temp.getString("content"));
                    }

                    if (jsonResponse.getJSONObject("header").getInteger("status") == 2)
                    {
                        webSocket.close(1000, "");
                        // 返回总结结果
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response)
                {
                    t.printStackTrace();
                }
            });

            return ""; // 根据具体逻辑返回总结结果
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
