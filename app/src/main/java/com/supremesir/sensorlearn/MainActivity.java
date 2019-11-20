package com.supremesir.sensorlearn;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Vibrator vibrator;

    private TextView lightLevel;
    private TextView accelerateLevel;
    private TextView fieldLevel;
    private TextView tempLevel;
    private TextView lightText;
    private SeekBar lightBar;
    private Button gravityBall;
    private Button compass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightLevel = findViewById(R.id.light_level);
        accelerateLevel = findViewById(R.id.accelerate_level);
        fieldLevel = findViewById(R.id.field_level);
        tempLevel = findViewById(R.id.temp_level);
        lightBar = findViewById(R.id.light_bar);
        lightText = findViewById(R.id.light_text);
        gravityBall = findViewById(R.id.gravity_ball);
        compass = findViewById(R.id.compass);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//注册传感器事件
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//注册震动事件

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);//光线传感器
        Sensor sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//三轴加速度
        Sensor sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//地磁传感器
        Sensor sensor3 = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); //距离传感器

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener2, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener3, sensor3, SensorManager.SENSOR_DELAY_NORMAL);


        lightBar.setProgress(getScreenBrightness(this.getBaseContext()));//设置SeekBar数值为当前屏幕亮度
        lightText.setText(""+getScreenBrightness(this.getBaseContext()));//设置TextView文字显示为当前屏幕亮度
        //TODO:优化代码结构
        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动中数值的时候
             * @param fromUser 是否是由用户操作的
             * */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TextView显示选择数值
                lightText.setText(""+progress);
                //解决数值为0时，亮度调节失效的问题（若数值为0，则设置亮度为1）
                if (progress == 0) {
                    setBrightness(MainActivity.this, 1);
                } else {
                    setBrightness(MainActivity.this,progress);
                }
//                Toast.makeText(getApplicationContext(),""+progress,Toast.LENGTH_SHORT).show();//Toast弹出提示
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            } //按下时
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }//松开时
        });

        /**
         * 点击按钮跳转到重力球界面
         */
        gravityBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GravityBallActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 点击按钮跳转到指南针界面
         */
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * 判断是否开启自动亮度调节
     */
    private boolean isAutoBrightness(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕的亮度
     */
    private int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    private void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        //FIXME:修复当brightness传入0时，亮度调节失控的问题
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }


    /**
     * 监听光照强度传感器
     */
    private SensorEventListener listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //values数组中第一个下标的值就是当前的光照强度
            float light = event.values[0];
            lightLevel.setText("当前的光照强度是：" + light + "lx");//单位：勒克斯
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 监听三轴加速度传感器
     */
    private SensorEventListener listener1=new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSensorChanged(SensorEvent event) {

            StringBuilder acceleration = new StringBuilder();
            acceleration.append("三轴加速度："+event.values[0]+","+event.values[1]+","+event.values[2]);
            accelerateLevel.setText(acceleration.toString());

            /**
             * API26及以上手机震动事件实现
             * TODO:为api26以下的手机适配震动实现方法
             */
            if (event.values[0] > 15 || event.values[1] > 15 || event.values[2] > 15) {

                //该方法只能在api26及以上使用
                vibrator.vibrate(VibrationEffect.createOneShot(500,255)); //创建一次性震动事件
                Toast.makeText(getApplicationContext(),"摇一摇！",Toast.LENGTH_SHORT).show();//Toast弹出提示
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 监听磁场传感器
     */
    private SensorEventListener listener2=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            StringBuilder field = new StringBuilder();
            field.append("磁场："+event.values[0]+","+event.values[1]+","+event.values[2]);
            fieldLevel.setText(field.toString());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 监听距离传感器
     * 无遮挡时，event.values[0]>5
     */
    private SensorEventListener listener3=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            StringBuilder temps = new StringBuilder();

            if (event.values[0] > 5) {
                temps.append("距离传感器无遮挡");
                tempLevel.setTextColor(Color.BLACK);
                tempLevel.setText(temps.toString());
            }
            if (event.values[0] < 5) {
                temps.append("距离传感器被遮挡");
                tempLevel.setTextColor(Color.RED);
                tempLevel.setText(temps.toString());
                Toast.makeText(getApplicationContext(),"距离传感器被遮挡！",Toast.LENGTH_SHORT).show();//Toast弹出提示
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * 程序结束时注销注册过的传感器监听
     */
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            //取消注册传感器事件
            sensorManager.unregisterListener(listener);
            sensorManager.unregisterListener(listener1);
            sensorManager.unregisterListener(listener2);
            sensorManager.unregisterListener(listener3);
            //取消注册震动事件
            vibrator.cancel();
        }
    }


}
