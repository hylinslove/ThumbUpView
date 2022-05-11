package com.bizgo.thumbsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private AppCompatButton thumbButton2;
    private ThumbUpView thumbUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                thumbUpView.addFavor();
                return false;
            }
        });
        AppCompatButton thumbButton = findViewById(R.id.thumb_button);
        thumbButton2 = findViewById(R.id.thumb_button2);

        thumbUpView = findViewById(R.id.thumb_view);
        thumbUpView.addImageRes(R.mipmap.face1);
        thumbUpView.addImageRes(R.mipmap.face2);
        thumbUpView.addImageRes(R.mipmap.face3);
        thumbUpView.addImageRes(R.mipmap.face4);
        thumbUpView.addImageRes(R.mipmap.face5);
        thumbUpView.addImageRes(R.mipmap.heart);


        thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbUpView.addFavor();
            }
        });

        thumbButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private void startTimer() {
        if (thumbButton2.getText().equals("持续点赞")) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
            timer.schedule(timerTask, 0, 300);
            thumbButton2.setText("取消");
        } else {
            thumbButton2.setText("持续点赞");
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    @Override
    protected void onPause() {
        if (thumbButton2.getText().equals("取消")) {
            thumbButton2.setText("持续点赞");
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
        super.onDestroy();
    }
}