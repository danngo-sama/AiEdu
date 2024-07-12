package online.manongbbq.aieducation.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import online.manongbbq.aieducation.activity.ChatActivity;
import online.manongbbq.aieducation.activity.CourseActivity;
import online.manongbbq.aieducation.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView textViewDateTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_stu, container, false);

        textViewDateTime = view.findViewById(R.id.textViewDateTime);
        ImageView imageView = view.findViewById(R.id.imageView5);
        ImageView imageView2 = view.findViewById(R.id.imageView3);


        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CourseActivity.class);
            startActivity(intent);
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };

        handler.post(runnable); // 启动定时任务

        return view;
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        String displayText = "欢迎回来！\n今天是" + currentDateTime + "\n今天你学习了吗？";
        textViewDateTime.setText(displayText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // 防止内存泄漏，移除回调
    }
}