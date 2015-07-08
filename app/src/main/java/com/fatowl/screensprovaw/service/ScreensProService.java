package com.fatowl.screensprovaw.service;

import java.io.File;
import java.io.IOException;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.fatowl.screensprovaw.main.Constants;
import com.fatowl.screensprovaw.main.InitialActivity;
import com.fatowl.screensprovaw.main.video.MyDebug;
import com.fatowl.screensprovaw.main.video.NativeCalls;
import com.fatowl.screensprovaw.main.video.VideoRenderer;
import com.fatowl.screensprovaw.main.video.VideoRenderer2;

public class ScreensProService extends GLWallpaperService {

    public static final int SCALING_MODE_0 = 0;
    public static final int SCALING_MODE_1 = 1;
    public static final int SCALING_MODE_2 = 2;
    public static final int SCALING_MODE_3 = 3;


    String currentVideoFile = "";
    boolean isVideoLoaded = false;
    long fps = 1000 / 30;
    SharedPreferences settings;

    public int scalingMode = SCALING_MODE_3;

    public ScreensProService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("TAG", "onCreate");

        settings = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);

        NativeCalls.closeVideo();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        NativeCalls.closeVideo();
    }

    CubeEngine mEngine = null;

    @Override
    public Engine onCreateEngine() {
        Log.v("onCreateEngine", "onCreateEngine");
        mEngine = new CubeEngine();
        return mEngine;
    }

    public class CubeEngine extends GLWallpaperService.GLEngine {

        boolean mVisible;
        boolean isAlive=true;
        VideoRenderer renderer = null;
        Handler mHandler = new Handler();

        boolean isInitialized = false;

        Runnable mDrawFrame = new Runnable() {

            @Override
            public void run() {
                drawFrame();
            }
        };

        public CubeEngine() {
            renderer = new VideoRenderer(this);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        public void InitializeVideo() {
            Log.v("ScreensProService", "Initialize Video");



            NativeCalls.closeVideo();


            NativeCalls.initVideo();
            String filename = settings.getString(Constants.VIDEO_NAME, null);


            Log.v("Fileee naaaame", "file name: " + filename);
            if (filename == null || filename.equals("")) {

                Intent intent = new Intent(getApplicationContext(), InitialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }



//            if(settings.getBoolean(Constants.WALLPAPAER_CANCELLED, false)){
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putBoolean(Constants.WALLPAPAER_CANCELLED, false);
//                editor.commit();
//                Intent intent = new Intent(getApplicationContext(), InitialActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                this.onDestroy();
//
//            }


            File videoFile = new File(filename);
            if (!videoFile.exists())
                return;


            NativeCalls.loadVideo("file:/" + filename);
            //NativeCalls.setTextureDimensions(NativeCalls.getVideoWidth(), NativeCalls.getVideoHeight());



            fps = 1000 / settings.getInt("FRAME_RATE", 33);


            String scaling = settings.getString("SCALE_TYPE", "SCALE_TO_FILL");

            if (scaling.equals("SCALE_TO_FILL")) {
                scalingMode = SCALING_MODE_3;
            } else if (scaling.equals("SCALE_TO_FIT")) {
                scalingMode = SCALING_MODE_0;
            }


            isInitialized = true;

        }

        public void UpdateDisplaying(int screenWidth, int screenHeight) {

            Log.v("ScreensProService", "Update Displaying");
            int videoWidth = NativeCalls.getVideoWidth();
            int videoHeight = NativeCalls.getVideoHeight();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            screenHeight = height;
            screenWidth = width;

            int scaledWidth, scaledHeight;
            int paddingX = 0, paddingY = 0;

            scaledWidth = screenWidth;
            scaledHeight = videoHeight * scaledWidth / videoWidth;

            if (scalingMode == SCALING_MODE_0) {
                if (scaledHeight < screenHeight) {
                    scaledHeight = screenHeight;
                    scaledWidth = videoWidth * scaledHeight / videoHeight;

                    paddingX = -(scaledWidth - screenWidth) / 2;
                    paddingY = 0;
                } else if (scaledHeight > screenHeight) {
                    paddingY = -(scaledHeight - screenHeight) / 2;
                    paddingX = 0;
                }
            } else if (scalingMode == SCALING_MODE_1) {
                if (scaledHeight > screenHeight) {
                    scaledHeight = screenHeight;
                    scaledWidth = videoWidth * scaledHeight / videoHeight;

                    paddingX = (screenWidth - scaledWidth) / 2;
                    paddingY = 0;
                } else if (scaledHeight < screenHeight) {
                    paddingY = (screenHeight - scaledHeight) / 2;
                    paddingX = 0;
                }
            } else if (scalingMode == SCALING_MODE_2) {
                scaledWidth = videoWidth;
                scaledHeight = videoHeight;

                paddingX = (screenWidth - scaledWidth) / 2;
                paddingY = (screenHeight - scaledHeight) / 2;
            } else {
                Log.v("Scaling Mode 3 selected","Scaling Mode 3 selected");
                scaledWidth = screenWidth;
                scaledHeight = screenHeight;

                paddingX = 0;
                paddingY = 0;
            }

            scaledWidth = screenWidth;
            scaledHeight = screenHeight;

            paddingX = (screenWidth - scaledWidth) / 2;
            paddingY = (screenHeight - scaledHeight) / 2;

            NativeCalls.getVideoWidth();
            NativeCalls.getVideoHeight();



           NativeCalls.setDrawDimensions(scaledWidth, scaledHeight);


            NativeCalls.setScreenPadding(paddingX, paddingY);

            NativeCalls.prepareStorageFrame();
            NativeCalls.initOpenGL();

        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v("onResume", "onResume");
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int w,
                                     int h) {
            Log.v("ScreensProService", "onSurfaceChanged");

            super.onSurfaceChanged(holder, format, w, h);

            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            Log.v("ScreensProService", "onSurfaceCreated");
            super.onSurfaceCreated(holder);

        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.v("ScreensProService", "onSurfaceDestroyed");
            super.onSurfaceDestroyed(holder);

            NativeCalls.closeOpenGL();
            NativeCalls.closePostOpenGL();
            NativeCalls.closeVideo();
            NativeCalls.freeVideo();


            if (renderer != null) {
                renderer.release();

                renderer = null;
            }

            if(mEngine!=null){
                mEngine.requestExitAndWait();
                mEngine.onDestroy();
            }

            onDestroy();

        }




        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.v("ScreensProService", "onVisibilityChanged to be "+visible);
            super.onVisibilityChanged(visible);

            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawFrame);
            }
        }

        public void drawFrame() {
            Log.v("ScreensProService", "drawFrame");
            mHandler.removeCallbacks(mDrawFrame);
            if (mVisible) {
                if (isInitialized)
                    requestRender();
                mHandler.postDelayed(mDrawFrame, fps);
            }
        }
    }


}