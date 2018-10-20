package com.hatem.myfirstandroidapp;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hatem.myfirstandroidapp.floatingwindowclass.floatingWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Button b1;
    private Button b2;
    public static int width;
    public static int height;
    private Button button;
    private TextView textView;
    private int counter;
    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private Intent broadcast;
    private Intent intent1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDimensions();

        this.toggleButton1 = new ToggleButton(this);
        this.toggleButton2 = new ToggleButton(this);
        this.toggleButton1 = findViewById(R.id.toggleButton);
        this.toggleButton2 = findViewById(R.id.toggleButton2);

        this.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                floatingWindow.sound = isChecked; // In case sound toggle value has changed
            }
        });

        this.toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                floatingWindow.vibration = isChecked; // In case vibration toggle value has changed
            }
        });

        //reading from log.txt which contains the number of nudges that have been displayed.
        this.button = new Button(this);
        this.textView = new TextView(this);
        this.button = findViewById(R.id.button);
        this.textView = findViewById(R.id.textView);
        this.button.setBackgroundColor(Color.argb(0,0,0,0));
        this.textView.setBackgroundColor(Color.argb(0,0,0,0));
        this.textView.setTextSize(30);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if(counter<3)
                    return;
                counter=0;
                File file = new File(getFilesDir(),"log.txt");
                String line = "";
                try {
                    FileInputStream fileInputStream = new FileInputStream (file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    line = bufferedReader.readLine();
                    fileInputStream.close();

                    bufferedReader.close();
                }
                catch(FileNotFoundException e) {
                    Log.d("FileNotFoundException", e.getMessage());
                }
                catch(IOException e) {
                    Log.d("IOException", e.getMessage());
                }
                textView.setText(line);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler mHandler = new Handler();
        final Intent intent = new Intent(MainActivity.this, floatingWindow.class); // The background service which is responsable of the camera view.
        intent1 = intent;


        Runnable start = new Runnable() {
            @Override
            public void run() {
                startService(intent);
            }
        };
        Runnable stop = new Runnable() {
            @Override
            public void run() {
                stopService(intent);
            }
        };
        mHandler.postDelayed(start, 10000);      // Starts the background service after 10 second from launching the app.
        mHandler.postDelayed(stop, 30 * 60000);  // Interrupts the background service after 30 minutes.


    }
    private void setDimensions(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent1);
        int id= android.os.Process.myPid();
        android.os.Process.killProcess(id);
    }


}

