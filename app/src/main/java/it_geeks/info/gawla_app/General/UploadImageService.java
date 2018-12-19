package it_geeks.info.gawla_app.General;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class UploadImageService extends Service {

    private static final String TAG = "UploadImageService";

    private IBinder imageBinder = new ImageBinder();
    private Handler handler;
    private int progress;
    private boolean isPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        progress = 0;
        isPaused = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return imageBinder;
    }

    public class ImageBinder extends Binder {

        public UploadImageService getService() {
            return UploadImageService.this;
        }
    }

    public void startTask() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    Log.d(TAG, "run: removing callbacks.");
                    handler.removeCallbacks(this);
                    pauseTask();
                } else {
                    Log.d(TAG, "run: progress: " + progress);
                    progress += 100;
                    handler.postDelayed(this, 100);
                }
            }
        };

        handler.postDelayed(runnable, 100);
    }

    public void pauseTask() {
        isPaused = true;
    }

    public void unPauseTask() {
        isPaused = false;
        startTask();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void resetTask() {
        progress = 0;
    }
}
