package com.hatem.myfirstandroidapp.floatingwindowclass;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hatem.myfirstandroidapp.MThread;
import com.hatem.myfirstandroidapp.MainActivity;
import com.hatem.myfirstandroidapp.R;
import com.hatem.myfirstandroidapp.previewClass.previewClass;

public class floatingWindow extends Service {
    private  WindowManager wm;
    private RelativeLayout rl2;
    private  RelativeLayout rl;
    private  previewClass cameraPreview;
    private int screenHeight;
    private int screenWidth;
    private  MThread MThread;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private MediaPlayer mp;
    private Vibrator v;
    public static boolean sound = false;
    public static boolean vibration = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.MThread = new MThread(this);   //Creating the timer
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        rl2 = new RelativeLayout(this);
        rl = new RelativeLayout(this);
        this.rl.setVisibility(View.INVISIBLE);
        this.imageView = new ImageView(this);
        this.imageView2 = new ImageView(this);
        this.imageView3 = new ImageView(this);
        this.imageView4 = new ImageView(this);
        screenHeight = MainActivity.height;
        screenWidth = MainActivity.width;


        //Setting image views which contain app icons.
        imageView.setImageResource(R.drawable.facebook);
        imageView2.setImageResource(R.drawable.snapchat);
        imageView3.setImageResource(R.drawable.fb_processing_gif);
        imageView4.setImageResource(R.drawable.sc_processing_gif);
        imageView.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams rlParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setBackgroundColor(Color.argb(100,255,0,0));
        rl.setLayoutParams(rlParameters);

        //Setting the floating window parameters to be not focusable so user can interact with the uncovered part of the screen normally.
        WindowManager.LayoutParams parameters2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("Floating window","Floating");
             parameters2 = new WindowManager.LayoutParams(((168*screenWidth)/1080),((screenHeight*300)/1920),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        }else{
              parameters2 = new WindowManager.LayoutParams(((168*screenWidth)/1080),((screenHeight*300)/1920),WindowManager.LayoutParams.TYPE_TOAST,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        }
        final WindowManager.LayoutParams parameters = parameters2;
        parameters.x = (600*screenWidth)/1440;
        parameters.y = (-800*screenHeight)/2712;
        parameters.gravity = Gravity.CENTER;

        //Editing positions of views within the relative layout
        RelativeLayout.LayoutParams cameraParameters = new RelativeLayout.LayoutParams(((168*screenWidth)/1080),((screenHeight*220)/1920));
        cameraParameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        RelativeLayout.LayoutParams rl2Parameters = new RelativeLayout.LayoutParams(((168*screenWidth)/1080),((screenHeight*80)/1920));
        rl2Parameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        //Editing icons' positions within the inner relative layout
        RelativeLayout.LayoutParams iconParameters = new RelativeLayout.LayoutParams(((80*screenWidth)/1080),((screenHeight*80)/1920));
        RelativeLayout.LayoutParams iconParameters2 = new RelativeLayout.LayoutParams(((80*screenWidth)/1080),((screenHeight*80)/1920));
        RelativeLayout.LayoutParams iconParameters3 = new RelativeLayout.LayoutParams(((60*screenWidth)/1080),((screenHeight*60)/1920));
        iconParameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iconParameters2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


        //Adding icons to the inner relative layout
        rl2.addView(imageView2,iconParameters);
        rl2.addView(imageView,iconParameters);
        rl2.addView(imageView3,iconParameters2);
        rl2.addView(imageView4,iconParameters2);

        //Creating a CameraPreview classs which is a surface containing the camera feed.
        cameraPreview = new previewClass(this);

        //Adding views to the outer relative layout
        rl.addView(cameraPreview,cameraParameters);
        rl.addView(rl2,rl2Parameters);
        wm.addView(rl,parameters);

        //Making the window draggable
        rl.setOnTouchListener(new View.OnTouchListener(){
            private WindowManager.LayoutParams updatedParameters = parameters;
            int x,y;
            float touchedX,touchedY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        touchedX = motionEvent.getRawX();
                        touchedY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x+ (motionEvent.getRawX() - touchedX));
                        updatedParameters.y = (int) (y+ (motionEvent.getRawY() - touchedY));

                        wm.updateViewLayout(rl,updatedParameters);
                    default: break;

                }
                return false;
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver2,
                new IntentFilter("activity-destroyed"));
        this.MThread.start();
        this.mp = MediaPlayer.create(this, R.raw.sound);
        this.v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean stopService(Intent name) {
      return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        rl.removeAllViews();
        this.MThread.interrupt();
        if(cameraPreview.camera !=null){
            cameraPreview.camera.stopPreview();
            cameraPreview.camera.release();}
        wm.removeView(rl);
        super.onDestroy();
    }

    //Receiving broadcasts from MThread
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if(message=="FBAccess")
                FBAccess();
            if(message=="SCAccess")
                SCAccess();
            if(message=="FBProcessing")
                FBProcessing();
            if(message=="SCProcessing")
                SCProcessing();
            if(message=="hideCameraView")
                hideCameraView();
            String message2 = intent.getStringExtra("message2");

            if(message2=="FBAccess")
                CancelFBAccess();
            if(message2=="SCAccess")
                CancelSCAccess();
            if(message2=="FBProcessing")
                CancelFBProcessing();
            if(message2=="SCProcessing")
                CancelSCProcessing();
        }
    };
    private BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message == "activityDestroyed"){
                onDestroy();
            Log.d("Floating window","broadcast received");
            }
        }
    };
    private void FBAccess(){
        imageView.setVisibility(View.VISIBLE);
        this.rl.setVisibility(View.VISIBLE);
        soundAndVibration();
    }
    private void SCAccess(){
        this.rl.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        soundAndVibration();
    }
    private void FBProcessing(){
        this.rl.setVisibility(View.VISIBLE);
        imageView3.setVisibility(View.VISIBLE);
        soundAndVibration();
    }
    private void SCProcessing(){
        this.rl.setVisibility(View.VISIBLE);
        imageView4.setVisibility(View.VISIBLE);
        soundAndVibration();
    }
    private void hideCameraView(){
        imageView.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
        this.rl.setVisibility(View.INVISIBLE);
    }
    private void CancelFBAccess(){
        imageView.setVisibility(View.INVISIBLE);
    }
    private void CancelSCAccess(){
        imageView2.setVisibility(View.INVISIBLE);
    }
    private void CancelFBProcessing(){
        imageView3.setVisibility(View.INVISIBLE);
    }
    private void CancelSCProcessing() {
        imageView4.setVisibility(View.INVISIBLE);
    }
    private void vibrate(){
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            this.v.vibrate(500);
        }
    }
    private void soundAndVibration(){
        if(sound)
            mp.start();
        if(vibration)
            vibrate();
    }
}
