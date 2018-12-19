package it_geeks.info.gawla_app.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import it_geeks.info.gawla_app.General.UploadImageService;

public class UploadImageViewModel extends ViewModel {

    private static final String TAG = "UploadImageViewModel";

    private MutableLiveData<Boolean> isProgressUpdating = new MutableLiveData<>();
    private MutableLiveData<UploadImageService.ImageBinder> binder = new MutableLiveData<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: connected to service");
            UploadImageService.ImageBinder imageBinder = (UploadImageService.ImageBinder) iBinder;
            binder.postValue(imageBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
             binder.postValue(null);
        }
    };

    public LiveData<Boolean> getIsProgressUpdating() {
        return isProgressUpdating;
    }

    public LiveData<UploadImageService.ImageBinder> getBinder() {
        return binder;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setIsProgressUpdating(Boolean isProgressUpdating) {
        this.isProgressUpdating.postValue(isProgressUpdating);
    }
}
