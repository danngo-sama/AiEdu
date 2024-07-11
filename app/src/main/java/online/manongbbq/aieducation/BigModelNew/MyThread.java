package online.manongbbq.aieducation.BigModelNew;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Iterator;
import java.util.UUID;
import okhttp3.WebSocket;
import online.manongbbq.aieducation.BigModelNew.RoleContent;

public class MyThread extends Thread {
    private WebSocket webSocket;

    BigModelNew this$0;

    public MyThread(BigModelNew var1, WebSocket webSocket){
        this.this$0 = var1;
        this.webSocket = webSocket;
    }

    public void run(){
        try{
            JSONObject requestJson = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("app_id", "");
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));
            JSONObject parameter = new JSONObject();
            JSONObject chat = new JSONObject();
            chat.put("domain", "generalv2");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);
            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();
            RoleContent tempRoleContent;
            if(BigModelNew.historyList.size() > 0){
                Iterator var9 = BigModelNew.historyList.iterator();
                while (var9.hasNext()){
                    tempRoleContent = (RoleContent)var9.next();
                    text.add(JSON.toJSON(tempRoleContent));
                }
            }
            tempRoleContent = new RoleContent(this.this$0);
            tempRoleContent.role = "user";
            tempRoleContent.content = BigModelNew.NewQuestion;
            text.add(JSON.toJSON(tempRoleContent));
            BigModelNew.historyList.add(tempRoleContent);
            message.put("text", text);
            payload.put("message", message);
            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);
            this.webSocket.send(requestJson.toString());

            do{
                Thread.sleep(200L);
            }while (!BigModelNew.access$0(this.this$0));

            this.webSocket.close(1000, "");
        }catch (Exception var10){
            var10.printStackTrace();
        }
    }
}
