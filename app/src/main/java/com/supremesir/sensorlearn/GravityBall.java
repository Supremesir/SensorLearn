package com.supremesir.sensorlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.content.Context.SENSOR_SERVICE;

/**
 * @author HaoFan Fang
 * @date 2019-11-20 10:33
 */

public class GravityBall extends SurfaceView implements SurfaceHolder.Callback,Runnable, SensorEventListener {

    final static  String TAG="mysuferView";
    SensorManager mSensorManager;//SensorManager 是系统所有传感器的管理器，获得一个sensormanager的实例
    Sensor mSensor_of_orientation;//传感器sensor实例
    //永远记得确保当你不需要的时候，特别是Activity暂定的时候，要关闭感应器。
    SensorEventListener mSensorEventListener;//对传感器输出的信号进行监听
    float GX=0;
    float GY=0;
    float GZ;

    /**每30帧刷新一次屏幕**/
    public static final int TIME_IN_FRAME = 30;


    Paint mPaint = null;
    Paint mLinePaint = null;
    /**小球资源文件**/
    private Bitmap mbitmapBall;
    /**游戏背景文件**/
    private Bitmap mbitmapBackground;




    SurfaceHolder mSurfaceHolder = null;
    /** 控制游戏更新循环 **/
    boolean mRunning = false; // 子线程标志位

    private Canvas mCanvas=null;// 用于绘图的Canvas

    /**手机屏幕宽高**/
    int mphoneScreenWidth = 0;
    int mphoneScreenHeight = 0;


    /**小球的坐标位置**/
    private float mPosX =500;
    private float mPosY =300;
    /*   不管横屏是由竖屏顺时针旋转90度，或者 逆时针旋转90度得到，
     都是以左上角为原点。是视觉上的左上角 ，不是空间上的左上角。
    */



    public void initView(Context context){//自定义的初始化方法

//        所谓焦点就是被选中的意思，或者说是“当前正在操作的组件”的意思。
//        如果一个组件被选中，或者正在被操作者，就是得到了焦点，而相反的，
// 一个组件没有被选中或者失去操作，就是被转移了焦点，焦点已经到别的组件上去了。
        this.setFocusable(true);
        //设置当前View可以拥有控制焦点

        /** 设置当前View拥有触摸事件 **/
        this.setFocusableInTouchMode(true);
        /** 拿到SurfaceHolder对象 **/
        mSurfaceHolder = this.getHolder();
        /** 将mSurfaceHolder添加到Callback回调函数中 **/
        mSurfaceHolder.addCallback(this);
        /** 创建画布 **/
        mCanvas = new Canvas();
        mCanvas.drawColor(Color.YELLOW);

        /** 创建画笔 ,分别用来画小球和杆**/
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mLinePaint=new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setTextSize(30);
        /**加载游戏背景**/
        mbitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        /**加载小球资源**/
        mbitmapBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball);


        mSensorManager= (SensorManager)context.getSystemService(SENSOR_SERVICE);//获取到 SensorManager 的实例
        mSensor_of_orientation=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获取重力传感器
        mSensorManager.registerListener(this, mSensor_of_orientation, SensorManager.SENSOR_DELAY_GAME);//注册传感器

    }
    private void Draw() {//绘制方法



        Log.e(TAG,mPosX+"mPosX ");
        Log.e(TAG,mPosY+" mPosY");
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        mPosX+=0.5*GY*(TIME_IN_FRAME*TIME_IN_FRAME/80);
        mPosY+=0.5*GX*(TIME_IN_FRAME*TIME_IN_FRAME/80);
        /**绘制游戏背景**/
        mCanvas.drawBitmap(mbitmapBackground,0,0, mPaint);
        Paint linshipaint=new Paint();
        linshipaint.setColor(Color.RED);
        linshipaint.setTextSize(50);


        mCanvas.drawText(GX+"  X轴上的重力加速度",0,30,mLinePaint);
        mCanvas.drawText(GY+"  Y轴上的重力加速度",0,80,mLinePaint);


        /**绘制小球**/
        mCanvas.drawBitmap(mbitmapBall, mPosX,mPosY, mPaint);
    }


    public GravityBall(Context context) {
        super(context);
        initView(context);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //              onSensorChanged()方法中传入了一个 SensorEvent 参数，
//              这个参数里又包含了一个 values 数组，所有传感器输出的信息都是存放在这里的
//                //获取传感器的数据
        GZ=event.values[2];
        GX=event.values[0];
        GY=event.values[1];


        Log.d(TAG," "+GX+"  X轴上的重力加速度");
        Log.d(TAG," "+GY+" Y轴上的重力加速度 ");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        /**开始游戏主循环线程**/
        mRunning = true;
        /**得到当前屏幕宽高**/
        mphoneScreenWidth = this.getWidth();
        mphoneScreenHeight = this.getHeight();
        Log.e(TAG,mphoneScreenWidth+"当前屏幕宽");
        Log.e(TAG,mphoneScreenHeight+"当前屏幕的高");


        new Thread(this).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mRunning = false;
        mSensorManager.unregisterListener(mSensorEventListener);//要调用 unregisterListener ()方法将使用的资源释放掉

    }

    @Override
    public void run() {

        while (mRunning) {

            /** 取得更新游戏之前的时间 **/
            long startTime = System.currentTimeMillis();

            /** 在这里加上线程安全锁 **/
            synchronized (mSurfaceHolder) {
                /** 拿到当前画布 然后锁定 **/
                mCanvas = mSurfaceHolder.lockCanvas();
                Draw();
                /** 绘制结束后解锁显示在屏幕上 **/
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }

            /** 取得更新游戏结束的时间 **/
            long endTime = System.currentTimeMillis();

            /** 计算出游戏一次更新的毫秒数 **/
            int diffTime = (int) (endTime - startTime);

            /** 确保每次更新时间为30帧 **/
            while (diffTime <= TIME_IN_FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                /** 线程等待 **/
                Thread.yield();
            }

        }

    }
}
