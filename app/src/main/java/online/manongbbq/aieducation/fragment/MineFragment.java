package online.manongbbq.aieducation.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.manongbbq.aieducation.activity.MessagesActivity;
import online.manongbbq.aieducation.R;

import android.content.Intent;

import android.widget.Button;
import android.widget.TextView;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.activity.AboutActivity;
import online.manongbbq.aieducation.activity.FaceInfoActivity;
import online.manongbbq.aieducation.activity.VersionInfoActivity;

public class MineFragment extends Fragment {

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_stu, container, false);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewStudentID = view.findViewById(R.id.textViewStudentID);
        Button buttonMessages = view.findViewById(R.id.buttonMessages);
        Button buttonFaceInfo = view.findViewById(R.id.buttonFaceInfo);
        Button buttonVersionInfo = view.findViewById(R.id.buttonVersionInfo);
        Button buttonAbout = view.findViewById(R.id.buttonAbout);

        textViewName.setText("姓名: 张三");
        textViewStudentID.setText("学工号: 20210001");

        buttonMessages.setOnClickListener(v -> startActivity(new Intent(getActivity(), MessagesActivity.class)));
        buttonFaceInfo.setOnClickListener(v -> startActivity(new Intent(getActivity(), FaceInfoActivity.class)));
        buttonVersionInfo.setOnClickListener(v -> startActivity(new Intent(getActivity(), VersionInfoActivity.class)));
        buttonAbout.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutActivity.class)));

        return view;
    }
}
