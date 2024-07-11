package online.manongbbq.aieducation.BigModelNew;
// Source code is decompiled from a .class file using FernFlower decompiler.

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import online.manongbbq.aieducation.BigModelNew.RoleContent;

public class BigModelNew extends WebSocketListener {
    public static final String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
    public static final String appid = "";
    public static final String apiSecret = "";
    public static final String apiKey = "";
    public static List<RoleContent> historyList = new ArrayList();
    public static String totalAnswer = "";
    public static String NewQuestion = "";
    public static final Gson gson = new Gson();
    private String userId;
    private Boolean wsCloseFlag;
    private static Boolean totalFlag = true;

    public BigModelNew(String userId, Boolean wsCloseFlag) {
        this.userId = userId;
        this.wsCloseFlag = wsCloseFlag;
    }

    public static void main(String[] args) throws Exception {
        while(true) {
            if (totalFlag) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("\ufffd\ufffd\u951b\ufffd");
                totalFlag = false;
                NewQuestion = scanner.nextLine();
                String authUrl = getAuthUrl("https://spark-api.xf-yun.com/v2.1/chat", "", "");
                OkHttpClient client = (new OkHttpClient.Builder()).build();
                String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
                Request request = (new Request.Builder()).url(url).build();

                for(int i = 0; i < 1; ++i) {
                    totalAnswer = "";
                    client.newWebSocket(request, new BigModelNew(String.valueOf(i), false));
                }
            } else {
                Thread.sleep(200L);
            }
        }
    }

    public static boolean canAddHistory() {
        int history_length = 0;

        RoleContent temp;
        for(Iterator var2 = historyList.iterator(); var2.hasNext(); history_length += temp.content.length()) {
            temp = (RoleContent)var2.next();
        }

        if (history_length > 12000) {
            historyList.remove(0);
            historyList.remove(1);
            historyList.remove(2);
            historyList.remove(3);
            historyList.remove(4);
            return false;
        } else {
            return true;
        }
    }

    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        System.out.print("\u6fb6\u0444\u0101\ufffd\ufffd\u951b\ufffd");
        MyThread myThread = new MyThread(this, webSocket);
        myThread.start();
    }

    public void onMessage(WebSocket webSocket, String text) {
        JsonParse myJsonParse = (JsonParse)gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            System.out.println("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u7487\ufffd\u951b\ufffd\ufffd\ufffd\u7487\ufffd\ufffd\ufffd\u6d93\u7334\ufffd" + myJsonParse.header.code);
            System.out.println("\ufffd\ufffd\u5a06\u00a4\ufffd\u950b\ufffd\ufffd\ufffdsid\u6d93\u7334\ufffd" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }

        List<Text> textList = myJsonParse.payload.choices.text;

        Text temp;
        for(Iterator var6 = textList.iterator(); var6.hasNext(); totalAnswer = totalAnswer + temp.content) {
            temp = (Text)var6.next();
            System.out.print(temp.content);
        }

        if (myJsonParse.header.status == 2) {
            System.out.println();
            System.out.println("*************************************************************************************");
            RoleContent roleContent;
            if (canAddHistory()) {
                roleContent = new RoleContent(this);
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            } else {
                historyList.remove(0);
                roleContent = new RoleContent(this);
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }

            this.wsCloseFlag = true;
            totalFlag = true;
        }

    }

    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);

        try {
            if (response != null) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "GET " + url.getPath() + " HTTP/1.1";
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = ((HttpUrl)Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath()))).newBuilder().addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).addQueryParameter("date", date).addQueryParameter("host", url.getHost()).build();
        return httpUrl.toString();
    }
}
