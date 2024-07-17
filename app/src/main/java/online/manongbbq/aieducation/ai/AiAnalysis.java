package online.manongbbq.aieducation.ai;

import android.content.Context;
import android.util.Log;

import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMOutput;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

public class AiAnalysis
{
    private static final String TAG = "AiAnalysis";
    private static final String APP_ID = "c72c8624"; // appid
    private static final String API_SECRET = "YWVmODZlNTAxOTY0OWUwZDEzYjk0OGQw"; // apiSecret
    private static final String API_KEY = "195606ee6f8cc7b485c37a59d7d6d65e"; // apiKey

    private LLM llm;
    private Context context;

    public AiAnalysis(Context context){
        this.context = context;
        initializeSdk();
    }

    private void initializeSdk(){
        SparkChainConfig config = SparkChainConfig.builder()
                .appID(APP_ID)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET);


        int ret = SparkChain.getInst().init(context, config);
        Log.d(TAG, "SDK init: " + ret);

        LLMConfig llmConfig = LLMConfig.builder()
                .domain("generalv3.5")
                .url("wss://spark-api.xf-yun.com/v3.5/summary");


        llm = new LLM(llmConfig);
    }

    public String analyzeText(String text)
    {
        if (llm == null) {
            Log.e(TAG, "LLM not initialized.");
            return "Initialization error";
        }

        // 添加前置说明
        String prompt = "请分析以下错题描述，返回这些错题的领域、涉及知识点以及易错点：\n" + text;

        LLMOutput output = llm.run(prompt);
        if(output.getErrCode() == 0){
            Log.i(TAG, "Sync call: " + output.getRole() + ": " + output.getContent());
            return parseAnalysisResult(output.getContent());
        }else{
            Log.e(TAG, "Sync call: " + output.getErrCode() + " " + output.getErrMsg());
            return "Error: " + output.getErrMsg();
        }
    }

    private String parseAnalysisResult(String content) {
        // 假设返回的内容是一个 JSON 格式的字符串，可以使用 JSON 解析库来解析
        // 这里为了简化，假设返回的内容已经是一个格式化好的字符串
        return content;
    }
}