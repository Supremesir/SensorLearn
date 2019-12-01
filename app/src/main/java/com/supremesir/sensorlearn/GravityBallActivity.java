package com.supremesir.sensorlearn;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

public class GravityBallActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏显示窗口
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();//隐藏ActionBar标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //加载布局文件要放在setFlags之后
        setContentView(R.layout.activity_gravity_ball);



        //强制横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        GravityBall gravityBall = new GravityBall(this);
        setContentView(gravityBall);
//        super.onResume();

    }




}
