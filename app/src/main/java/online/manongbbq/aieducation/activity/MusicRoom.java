package online.manongbbq.aieducation.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;

public class MusicRoom extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String[] songList = {"冥想", "时雨", "黄昏","江南","大海","大漠","海岸","雪舞"}; // 歌曲名称列表
    private int[] songResIds = {R.raw.mxjx, R.raw.syrm, R.raw.jmhh,R.raw.jnyx,R.raw.dhsc,R.raw.dmfs,R.raw.cphy,R.raw.fhxw}; // 歌曲资源ID列表
    private int currentSongIndex = -1; // 当前选中歌曲索引
    private boolean isPlaying = false; // 播放状态

    private TextView textViewSongTitle;
    private ImageButton buttonPlayPause;
    private ListView listViewSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_room);

        textViewSongTitle = findViewById(R.id.textViewSongTitle);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        listViewSongs = findViewById(R.id.listViewSongs);

        // 设置歌曲列表适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        listViewSongs.setAdapter(adapter);

        // 监听歌曲列表点击事件
        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 选中歌曲但不播放
                currentSongIndex = position;
                textViewSongTitle.setText("选中歌曲：" + songList[position]);
                buttonPlayPause.setImageResource(R.drawable.ic_pause); // 确保播放图标
            }
        });

        // 播放/暂停按钮点击事件
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongIndex == -1) {
                    Toast.makeText(MusicRoom.this, "请先选择一首歌曲", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mediaPlayer != null && isPlaying) {
                    pauseSong();
                } else {
                    playCurrentSong();
                }
            }
        });
    }

    /**
     * 播放当前选中的歌曲
     */
    private void playCurrentSong() {
        if (currentSongIndex < 0 || currentSongIndex >= songResIds.length) {
            Toast.makeText(this, "请选择有效的歌曲", Toast.LENGTH_SHORT).show();
            return;
        }

        // 停止当前播放的歌曲
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // 创建新的MediaPlayer
        mediaPlayer = MediaPlayer.create(this, songResIds[currentSongIndex]);
        mediaPlayer.start();
        isPlaying = true;
        buttonPlayPause.setImageResource(R.drawable.ic_play); // 更新为暂停图标

        // 播放完后自动重置按钮状态
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonPlayPause.setImageResource(R.drawable.ic_pause);
                isPlaying = false;
            }
        });
    }

    /**
     * 暂停当前播放的歌曲
     */
    private void pauseSong() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            buttonPlayPause.setImageResource(R.drawable.ic_pause); // 更新为播放图标
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}