package online.manongbbq.aieducation.ai;

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
                .apiSecret(API_SECRET);

        int ret = SparkChain.getInst().init(context, config);
        Log.d(TAG, "SDK init: " + ret);

        LLMConfig llmConfig = LLMConfig.builder()
                .domain("generalv3.5")
                .url("wss://spark-api.xf-yun.com/v3.5/chat");

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