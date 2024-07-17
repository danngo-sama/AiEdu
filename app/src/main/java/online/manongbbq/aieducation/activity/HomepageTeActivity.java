package online.manongbbq.aieducation.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.fragment.HomeStuFragment;
import online.manongbbq.aieducation.fragment.HomeTeFragment;
import online.manongbbq.aieducation.fragment.MineStuFragment;
import online.manongbbq.aieducation.fragment.MineTeFragment;
import online.manongbbq.aieducation.fragment.ScheduleFragment;

public class HomepageTeActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_te);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeTeFragment();
                } else if (itemId == R.id.nav_mine) {
                    selectedFragment = new MineTeFragment();
                } else if (itemId == R.id.nav_schedule) {
                    selectedFragment = new ScheduleFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
                }
                return true;
            }
        });

        // 默认选择第一个页面
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}