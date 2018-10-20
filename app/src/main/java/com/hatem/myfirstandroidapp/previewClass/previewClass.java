package com.hatem.myfirstandroidapp.previewClass;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hatem.myfirstandroidapp.R;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class previewClass extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    public Camera camera;

    public previewClass(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);

    }

    //Once surface is created, I try to aquire the front camera object
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null){
            try {
                    camera = Camera.open(findFrontFacingCameraID());}
                    catch (Exception e){
                        Log.e(R.string.app_name +"", "failed to open camera!");
                    }
        }
        if(camera!=null) {
            try {
                camera.setPreviewDisplay(holder); //Setting the camera feed to my holder.
                camera.startPreview(); //starting getting the camera feed.
                camera.setDisplayOrientation(90); //Rotate the camera view by 90 degrees.

            } catch (IOException e) {
                camera.release();
                camera = null;
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //Once the surface holding the camera view is destroyed, the camera (hardware) should be released so that other apps can use it.
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera == null) {
            return;
        }
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    //This method returns the ID of the front facing camera in the mobile (IDs could be different in different mobiles)
    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info= new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

}