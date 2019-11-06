package com.supremesir.sensorlearn;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightLevel = findViewById(R.id.light_level);
        accelerateLevel = findViewById(R.id.accelerate_level);
        fieldLevel = findViewById(R.id.field_level);
        tempLevel = findViewById(R.id.temp_level);

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



    }

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

    private SensorEventListener listener1=new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSensorChanged(SensorEvent event) {

            StringBuilder acceleration = new StringBuilder();
            acceleration.append("三轴加速度："+event.values[0]+","+event.values[1]+","+event.values[2]);
            accelerateLevel.setText(acceleration.toString());


            //摇动手机震动
            if (event.values[0] > 25 || event.values[1] > 25 || event.values[2] > 25) {
                //TODO:为api26以下的手机适配震动实现方法

                //该方法只能在api26及以上使用
                vibrator.vibrate(VibrationEffect.createOneShot(500,255)); //创建一次性震动事件

                Toast.makeText(getApplicationContext(),"摇一摇！",Toast.LENGTH_SHORT).show();//Toast弹出提示

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

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
