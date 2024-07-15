package online.manongbbq.aieducation.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.activity.AiAnalyzeWrActivity;
import online.manongbbq.aieducation.data.WrBoDatabaseOperations;

public class WrongBookActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private LinearLayout mistakesContainer;
    private Uri photoURI;
    private WrBoDatabaseOperations db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrongbook);

        mistakesContainer = findViewById(R.id.mistakes_container);
        db = new WrBoDatabaseOperations(this);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());

        ImageButton aiAnalyzeButton = findViewById(R.id.button_ai_analyze);
        aiAnalyzeButton.setOnClickListener(v -> {
            Intent intent = new Intent(WrongBookActivity.this, AiAnalyzeWrActivity.class);
            startActivity(intent);
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

    private void displayMistakes(List<JSONObject> errorBookList) {
        for (JSONObject errorBook : errorBookList) {
            try {
                String imgPath = errorBook.getString("img_path");
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                String errorAnalysis = errorBook.getString("description");
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private void saveMistake(Bitmap bitmap, String description, Uri imageUri) {
        // 将Bitmap保存到本地文件
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 保存错题信息到数据库
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
}