package com.hatem.myfirstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

//This class acts as a timer which sends broadcasts to mock up front camera access.
public class MThread extends Thread {
    private Context context;
    private boolean flag = true;
    private Intent intent;
    private Random rnd = new Random();
    private int counter = 1;
    private int limit = 6;

    public MThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        this.intent = new Intent("custom-event-name");
        while (flag && limit!=0) {// Running untill flag = false which happens when the thread is interrupted
            int type = rnd.nextInt(6) + 1;
            if ( type == 1) {
                limit = limit-2;
                int fbLevel = rnd.nextInt(2) + 1;
                fbLevel = 1;
                notifyFBAccess();
                delay(10,15);
                CancelFBAccess();
                if (fbLevel == 1) {
                    //notify FB processing
                    notifyFBProcessing();
                    delay(10, 20);
                    CancelFBProcessing();
                }
            } else if ( type == 2) {
                limit = limit-2;
                int scLevel = rnd.nextInt(2) + 1;
                scLevel = 1;
                //notify access SC
                notifySCAccess();
                delay(10, 15);
                CancelSCAccess();

                if (scLevel == 1) {
                    //notify SC processing
                    notifySCProcessing();
                    delay(10, 20);
                    CancelSCProcessing();
                }
            } else {
                hideCameraView();
                delay(10,17);
            }
        }
    }

    //Generating random delay.
    private void delay(int min, int max) {
        int delay = rnd.nextInt(max+1 - min) + min;
        try {
            Thread.sleep(delay*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Sending a broadcast to the floating window to add the FB access icon.
    private void notifyFBAccess(){
        this.intent.putExtra("message", "FBAccess");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //Sending a broadcast to the floating window to add the SC access icon.
    private void notifySCAccess(){
        this.intent.putExtra("message", "SCAccess");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //Sending a broadcast to the floating window to add the FB processing icon.
    private void notifyFBProcessing(){
        this.intent.putExtra("message", "FBProcessing");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //Sending a broadcast to the floating window to add the SC processing icon.
    private void notifySCProcessing(){
        this.intent.putExtra("message", "SCProcessing");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        log((counter++)+"");
    }

    //Sending a broadcast to the floating window to hide the camera view.
    private  void hideCameraView(){
        this.intent.putExtra("message", "hideCameraView");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
    }
    private void CancelFBAccess(){
        this.intent.putExtra("message2", "FBAccess");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
    }

    private void CancelSCAccess(){
        this.intent.putExtra("message2", "SCAccess");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
    }

    private void CancelFBProcessing(){
        this.intent.putExtra("message2", "FBProcessing");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        hideCameraView();
    }

    private void CancelSCProcessing(){
        this.intent.putExtra("message2", "SCProcessing");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(this.intent);
        hideCameraView();
    }
    @Override
    public void interrupt() {
        this.flag=false;
    }

    //This function saves the number of nudges have been displayed in a text file in the app directory.
    public void log(String text)
    {
        File logFile = new File(this.context.getFilesDir(),"log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {   logFile.delete();
            logFile.createNewFile();
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.write(text);
            buf.flush();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}