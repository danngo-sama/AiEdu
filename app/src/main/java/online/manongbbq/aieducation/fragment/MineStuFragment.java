package online.manongbbq.aieducation.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.manongbbq.aieducation.activity.MainActivity;
import online.manongbbq.aieducation.activity.MessagesActivity;
import online.manongbbq.aieducation.R;

import android.content.Intent;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import online.manongbbq.aieducation.activity.AboutActivity;
import online.manongbbq.aieducation.activity.FaceInfoActivity;
import online.manongbbq.aieducation.activity.VersionInfoActivity;
import online.manongbbq.aieducation.information.NameCallback;
import online.manongbbq.aieducation.information.SessionManager;

public class MineStuFragment extends Fragment {
    private SessionManager sessionManager;

    public MineStuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_stu, container, false);
        sessionManager = SessionManager.getInstance(getActivity());

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewStudentID = view.findViewById(R.id.textViewStudentID);
        Button buttonMessages = view.findViewById(R.id.buttonMessages);
        Button buttonFaceInfo = view.findViewById(R.id.buttonFaceInfo);
        Button buttonVersionInfo = view.findViewById(R.id.buttonVersionInfo);
        Button buttonAbout = view.findViewById(R.id.buttonAbout);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish(); // 关闭当前活动，防止返回
        });


        sessionManager.getUserName(new NameCallback() {
            @Override
            public void onNameFound(String name) {
                textViewName.setText("姓名: " + name);
                textViewStudentID.setText("学工号: " + sessionManager.getUserId());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), "获取用户信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



//        textViewName.setText("姓名: 张三");
//        textViewStudentID.setText("学工号: 20210001");

        buttonMessages.setOnClickListener(v -> startActivity(new Intent(getActivity(), MessagesActivity.class)));
        buttonFaceInfo.setOnClickListener(v -> startActivity(new Intent(getActivity(), FaceInfoActivity.class)));
        buttonVersionInfo.setOnClickListener(v -> startActivity(new Intent(getActivity(), VersionInfoActivity.class)));
        buttonAbout.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutActivity.class)));

        return view;
    }
}
