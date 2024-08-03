package online.manongbbq.aieducation.activity;

import static online.manongbbq.aieducation.data.WrBoDatabaseOperations.COLUMN_DESCRIPTION;
import static online.manongbbq.aieducation.data.WrBoDatabaseOperations.COLUMN_IMG_PATH;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.activity.AiAnalyzeWrActivity;
import online.manongbbq.aieducation.ai.AiAnalysis;
import online.manongbbq.aieducation.ai.ImageToText;
import online.manongbbq.aieducation.ai.ImgToText;
import online.manongbbq.aieducation.ai.JsonParse;
import online.manongbbq.aieducation.ai.RobotAssistant;
import online.manongbbq.aieducation.ai.UniversalCharacterRecognition;
import online.manongbbq.aieducation.data.WrBoDatabaseOperations;

interface TextConversionCallback {
    void onTextConverted(String result);
}

public class WrongBookActivity extends AppCompatActivity implements ImgToText.OnTextResultListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int REQUEST_PERMISSION_CODE = 100;

    public TextView debugInfoTextView ;


    private LinearLayout mistakesContainer;
    private Uri photoURI;

    private RobotAssistant robotAssistant;
    private WrBoDatabaseOperations db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrongbook);

        mistakesContainer = findViewById(R.id.mistakes_container);
        db = new WrBoDatabaseOperations(this);
        debugInfoTextView= findViewById(R.id.text_title);
        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());


        ImageButton aiAnalyzeButton = findViewById(R.id.button_ai_analyze);
        aiAnalyzeButton.setOnClickListener(v -> {
//            appendDebugLog("步骤1");

            // 使用回调来处理异步任务的结果
            convertAllImagesToText(textc -> {
//                appendDebugLog("打印textc:" + textc);
                try {
                    showAiSummary(textc);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });


        Button uploadButton = findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImageOptions();
            }
        });

        // 查询并显示错题
        try {
            List<JSONObject> errorBookList = db.queryErrorBook();
            displayMistakes(errorBookList);
        } catch (JSONException e) {
            Toast.makeText(this, "加载错题失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAiSummary(String text) throws JSONException {
        List<JSONObject> errorBookList = db.queryErrorBook();

        if (errorBookList.isEmpty()) {
            Toast.makeText(this, "请先进行语音转文字", Toast.LENGTH_SHORT).show();
            return;
        }

        String text2 = "我下面会给你几道做错了的题目的文字版，你需要向我返回错题分析内容，注意不用详细分析，只需要概括性回复。包括这几道题所涉及的知识点、易错点、学习时候的注意点以及学习建议\n";

        String Text = text2 + text;
        robotAssistant = new RobotAssistant(this);

        String summary = robotAssistant.getAnswer(Text);

        // 加载自定义布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_scrollable_text, null);

        // 在自定义布局中找到 TextView
        TextView textViewSummary = dialogView.findViewById(R.id.textViewSummary);
        textViewSummary.setText(summary);

        // 构建并显示带有自定义布局的对话框
        new AlertDialog.Builder(this)
                .setTitle("AI总结")
                .setView(dialogView) // 设置自定义视图
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    showDescriptionDialog(bitmap, selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    showDescriptionDialog(bitmap, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void appendDebugLog(String message) {
        runOnUiThread(() -> debugInfoTextView.append(message + "\n"));
    }

    private void convertAllImagesToText(TextConversionCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            StringBuilder textb = new StringBuilder();
            List<JSONObject> errorBookList = null;
            try {
                errorBookList = db.queryErrorBook();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            for (JSONObject errorBook : errorBookList) {
                String imgPath = null;
                try {
                    imgPath = errorBook.getString(COLUMN_IMG_PATH);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                File imgFile = new File(imgPath);
                if (!imgFile.exists()) {
                    Log.e("ImagePath", "无效的图片路径: " + imgPath);
                    appendDebugLog("图片路径无效" );
                    continue;
                }
                // 如果文件存在，继续处理
            }


            try {

                if (errorBookList.isEmpty()) {
                    appendDebugLog("没有错题图片可处理\n");
                    handler.post(() -> debugInfoTextView.setText("没有错题图片可处理\n"));
                    callback.onTextConverted("");  // 回调空结果
                    return;
                }

                Gson gson = new Gson();
                for (JSONObject errorBook : errorBookList) {
                    String imgPath = errorBook.getString(COLUMN_IMG_PATH);
//                    File imageFile = new File(imgPath);
//
//                    appendDebugLog(imgPath);

                    UniversalCharacterRecognition demo = new UniversalCharacterRecognition();
                    demo.IMAGE_PATH = imgPath;

                    try {
                        String resp = demo.doRequest(this);

                        JsonParse myJsonParse = gson.fromJson(resp, JsonParse.class);

                        String textBase64Decode = new String(Base64.getDecoder().decode(myJsonParse.payload.result.text), "UTF-8");
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(textBase64Decode);

                        String texta = extractTextFromJson(jsonObject);

                        textb.append(texta).append("\n");
//                        appendDebugLog("识别结果1为：" + texta);

                    } catch (Exception e) {
                        appendDebugLog("出现错误: " + e.getMessage() + "\n");
                    }
                }
            } catch (JSONException e) {
                appendDebugLog("数据库查询错题失败: " + e.getMessage() + "\n");
            } finally {
                // 异步任务完成后，使用回调返回结果
                handler.post(() -> {
//                    appendDebugLog("识别结果b为：" + textb.toString());
                    callback.onTextConverted(textb.toString());
                });
            }
        });
    }



//    private String convertAllImagesToText() {
//        StringBuilder debugInfo = new StringBuilder("调试信息：\n");
//
//
//        final StringBuilder textb = new StringBuilder();
////        appendDebugLog("步骤-1: UI线程初始化成功\n");
//
//
//
//
//        // 立即更新UI，查看是否能成功显示到步骤-1
//        debugInfoTextView.setText(debugInfo.toString());
//
//        Executor executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//
//
////        appendDebugLog("步骤0: Executor和Handler初始化成功\n");
//
//        // 将步骤0的信息更新放到主线程队列，看是否有阻塞
//        handler.post(() -> debugInfoTextView.setText(debugInfo.toString()));
//
//        executor.execute(() -> {
//            try {
//                List<JSONObject> errorBookList = db.queryErrorBook();
//                if (errorBookList.isEmpty()) {
////                    debugInfo.append("没有错题图片可处理\n");
//                    appendDebugLog("没有错题图片可处理\n");
//                    handler.post(() -> debugInfoTextView.setText(debugInfo.toString()));
//                    return;
//                }
//
//                Gson gson = new Gson();
//                for (JSONObject errorBook : errorBookList) {
//                    String imgPath = errorBook.getString("img_path");
//                    File imageFile = new File(imgPath);
//
//
//
////                    appendDebugLog("开始识别图片路径: "+imgPath+"\n");
//
//                    UniversalCharacterRecognition demo = new UniversalCharacterRecognition();
//                    demo.IMAGE_PATH=imgPath;
//
////                    appendDebugLog("步骤1.1\n");
//                    demo.IMAGE_PATH = imgPath;
//                    try {
////
////                        appendDebugLog("步骤1: 开始操作\n");
//
//                        String resp = demo.doRequest(this);
//
////                        appendDebugLog("步骤2: 收到响应\n");
//
//                        JsonParse myJsonParse = gson.fromJson(resp, JsonParse.class);
//
////                        appendDebugLog("步骤3: JSON解析完成\n");
//
//
//                        String textBase64Decode = new String(Base64.getDecoder().decode(myJsonParse.payload.result.text), "UTF-8");
//                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(textBase64Decode);
////                        appendDebugLog("步骤4: 文本解码并解析为JSONObject\n");
//
////                        debugInfo.append("识别结束: ").append(jsonObject.toString()).append("\n");
//
////                        appendDebugLog("识别结束: "+jsonObject.toString()+"\n");
//
//                        String texta = "";
//
//                        texta = extractTextFromJson(jsonObject);
//
//                        textb.append(texta).append("\n");
////                        appendDebugLog("识别结果1为："+texta);
//
//
//
//                    } catch (Exception e) {
////                        debugInfo.append("出现错误: ").append(e.getMessage()).append("\n");
//
//                        appendDebugLog("出现错误: "+e.getMessage()+"\n");
//                    }
//                }
//            } catch (JSONException e) {
//                debugInfo.append("数据库查询错题失败: ").append(e.getMessage()).append("\n");
//            }
////            handler.post(() -> debugInfoTextView.setText(debugInfo.toString()));
//
//        });
//        appendDebugLog("识别结果b为："+textb.toString());
//        return textb.toString();
//    }



//    public static String extractTextFromJson(com.alibaba.fastjson.JSONObject jsonObject) {
//        StringBuilder resultText = new StringBuilder();
//
//        try {
//            // 从 JSON 中获取 pages 数组
//            com.alibaba.fastjson.JSONArray pages = jsonObject.getJSONArray("pages");
//
//            // 使用索引遍历 pages 数组
//            for (int pagesIndex = 0; ; pagesIndex++) {
//                try {
//                    com.alibaba.fastjson.JSONObject page = pages.getJSONObject(pagesIndex);
//
//                    // 从每个 page 中获取 lines 数组
//                    com.alibaba.fastjson.JSONArray lines = page.getJSONArray("lines");
//
//                    // 使用索引遍历 lines 数组
//                    for (int linesIndex = 0; ; linesIndex++) {
//                        try {
//                            com.alibaba.fastjson.JSONObject line = lines.getJSONObject(linesIndex);
//
//                            // 从每个 line 中获取 words 数组
//                            JSONArray words = line.getJSONArray("words");
//
//                            // 使用索引遍历 words 数组
//                            for (int wordsIndex = 0; ; wordsIndex++) {
//                                try {
//                                    com.alibaba.fastjson.JSONObject word = words.getJSONObject(wordsIndex);
//                                    String content = word.getString("content");
//                                    if (content != null) {
//                                        resultText.append(content);
//                                    }
//                                } catch (IndexOutOfBoundsException e) {
//                                    // 当索引超出范围时，退出当前的 words 循环
//                                    break;
//                                }
//                            }
//                            // 添加换行符以区分每一行
//                            resultText.append("\n");
//                        } catch (IndexOutOfBoundsException e) {
//                            // 当索引超出范围时，退出当前的 lines 循环
//                            break;
//                        }
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    // 当索引超出范围时，退出当前的 pages 循环
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("JSON 解析错误: " + e.getMessage());
//        }
//
//        return resultText.toString();
//    }

    public static String extractTextFromJson(com.alibaba.fastjson.JSONObject jsonObject) {
        StringBuilder resultText = new StringBuilder();

        try {
            // 从 JSON 中获取 pages 数组
            com.alibaba.fastjson.JSONArray pages = jsonObject.getJSONArray("pages");

            // 检查 pages 数组是否为空
            if (pages == null || pages.isEmpty()) {
                throw new NullPointerException("JSON 中缺少 'pages' 数组或数组为空");
            }

            // 使用索引遍历 pages 数组
            for (int pagesIndex = 0; pagesIndex < pages.size(); pagesIndex++) {
                com.alibaba.fastjson.JSONObject page = pages.getJSONObject(pagesIndex);

                // 从每个 page 中获取 lines 数组
                com.alibaba.fastjson.JSONArray lines = page.getJSONArray("lines");

                // 检查 lines 数组是否为空
                if (lines == null || lines.isEmpty()) {
                    continue; // 如果当前 page 中没有 lines，则跳过
                }

                // 使用索引遍历 lines 数组
                for (int linesIndex = 0; linesIndex < lines.size(); linesIndex++) {
                    com.alibaba.fastjson.JSONObject line = lines.getJSONObject(linesIndex);

                    // 从每个 line 中获取 words 数组
                    com.alibaba.fastjson.JSONArray words = line.getJSONArray("words");

                    // 检查 words 数组是否为空
                    if (words == null || words.isEmpty()) {
                        continue; // 如果当前 line 中没有 words，则跳过
                    }

                    // 使用索引遍历 words 数组
                    for (int wordsIndex = 0; wordsIndex < words.size(); wordsIndex++) {
                        com.alibaba.fastjson.JSONObject word = words.getJSONObject(wordsIndex);
                        String content = word.getString("content");
                        if (content != null) {
                            resultText.append(content);
                        }
                    }
                    // 添加换行符以区分每一行
                    resultText.append("\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析错误: " + e.getMessage());
        }

        return resultText.toString();
    }


//    private String extractTextFromJson(String jsonString) {
//        StringBuilder concatenatedText = new StringBuilder();
//
//        try {
//            // 将字符串解析为JSON对象
//            JSONObject jsonObject = new JSONObject(jsonString);
//            JSONObject payload = jsonObject.getJSONObject("payload");
//            JSONObject result = payload.getJSONObject("result");
//
//            // Base64解码后得到的JSON文本
//            String decodedTextJson = new String(java.util.Base64.getDecoder().decode(result.getString("text")), "UTF-8");
//            JSONObject decodedJsonObject = new JSONObject(decodedTextJson);
//
//            // 获取页面数组
//            JSONArray pages = decodedJsonObject.getJSONArray("pages");
//
//            // 遍历每一页
//            for (int i = 0; i < pages.length(); i++) {
//                JSONObject page = pages.getJSONObject(i);
//                JSONArray lines = page.getJSONArray("lines");
//
//                // 遍历每一行
//                for (int j = 0; j < lines.length(); j++) {
//                    JSONObject line = lines.getJSONObject(j);
//                    JSONArray words = line.getJSONArray("words");
//
//                    // 遍历每个单词
//                    for (int k = 0; k < words.length(); k++) {
//                        JSONObject word = words.getJSONObject(k);
//                        String content = word.getString("content");
//
//                        // 将单词内容拼接到结果字符串
//                        concatenatedText.append(content);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return concatenatedText.toString();
//    }

    private void displayMistakes(List<JSONObject> errorBookList) {
        for (JSONObject errorBook : errorBookList) {
            try {
                String imgPath = errorBook.getString(COLUMN_IMG_PATH);
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                String errorAnalysis = errorBook.getString(COLUMN_DESCRIPTION);
                int id = errorBook.getInt("id");

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(16, 16, 16, 16);
                imageView.setLayoutParams(layoutParams);
                imageView.setBackgroundResource(R.drawable.border); // 添加边框

                mistakesContainer.addView(imageView);

                TextView textView = new TextView(this);
                textView.setText("序号: " + id + "\n描述: " + errorAnalysis);
                mistakesContainer.addView(textView);

                // 添加删除按钮
                Button deleteButton = new Button(this);
                deleteButton.setText("删除");
                deleteButton.setOnClickListener(v -> {
                    // 调用删除函数
                    confirmDeleteMistake(id);
                });
                mistakesContainer.addView(deleteButton);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void confirmDeleteMistake(int id) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("您确定要删除此错题吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteMistake(id))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteMistake(int id) {
        // 从数据库中删除错题
        boolean deleted = db.deleteErrorBook(id);
        if (deleted) {
            Toast.makeText(this, "错题删除成功", Toast.LENGTH_SHORT).show();
            // 重新加载错题
            mistakesContainer.removeAllViews();
            try {
                List<JSONObject> errorBookList = db.queryErrorBook();
                displayMistakes(errorBookList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    private void showImageOptions() {
        // 选择上传方式：相册或拍照
        CharSequence[] options = {"从相册选择", "拍照"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("上传错题");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // 从相册选择
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (which == 1) {
                // 拍照
                dispatchTakePictureIntent();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 确保有相机应用来处理意图
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 创建一个文件来保存照片
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // 处理错误
                Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
                return;
            }
            // 继续只有在文件成功创建的情况下
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "online.manongbbq.aieducation.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // 创建一个唯一的图像文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 目录 */
        );

        // 保存文件路径以便于在onActivityResult中使用
        return image;
    }

    private void showDescriptionDialog(Bitmap bitmap, Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入错题描述");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String description = input.getText().toString();
            saveMistake(bitmap, description, imageUri);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

//    private void saveMistake(Bitmap bitmap, String description, Uri imageUri) {
//        // 将Bitmap保存到本地文件
//        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
//        try (FileOutputStream out = new FileOutputStream(imageFile)) {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 保存错题信息到数据库
//        db.insertErrorBook(imageFile.getAbsolutePath(), description);
//
//        // 刷新页面显示
//        mistakesContainer.removeAllViews();
//        try {
//            List<JSONObject> errorBookList = db.queryErrorBook();
//            displayMistakes(errorBookList);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void saveMistake(Bitmap bitmap, String description, Uri imageUri) {
        // 获取公共图片目录的路径
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // 确保目录存在，如果不存在则创建
        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (!created) {
                Log.e("SaveMistake", "无法创建图片存储目录");
                return;
            }
        }

        // 为图片生成唯一的文件名
        String imageFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(storageDir, imageFileName);

        // 压缩并保存Bitmap到文件
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            // 设置初始压缩质量
            int quality = 100;
            // 将Bitmap压缩到ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            // 循环压缩直到图像大小小于300KB
            while (byteArrayOutputStream.toByteArray().length / 1024 > 300 && quality > 0) {
                quality -= 5; // 每次减少压缩质量
                byteArrayOutputStream.reset(); // 重置输出流
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            }

            // 将压缩后的数据写入文件
            out.write(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将图片的绝对路径和描述存储到数据库中
        db.insertErrorBook(imageFile.getAbsolutePath(), description);

        // 刷新页面显示
        mistakesContainer.removeAllViews();
        try {
            List<JSONObject> errorBookList = db.queryErrorBook();
            displayMistakes(errorBookList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
                showImageOptions();
            } else {
                // Permissions denied
                Toast.makeText(this, "需要相机和存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResult(String result) {
        Toast.makeText(this, "识别内容："+result , Toast.LENGTH_LONG).show();
        StringBuilder allTexts = new StringBuilder();
        if (result != null && !result.isEmpty()) {
            allTexts.append(result).append("\n");
        }

    }
}