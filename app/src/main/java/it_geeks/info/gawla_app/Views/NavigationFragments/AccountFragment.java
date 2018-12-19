package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.General.UploadImageService;
import it_geeks.info.gawla_app.ViewModels.UploadImageViewModel;
import it_geeks.info.gawla_app.Views.AccountOptions.AccountDetails;
import it_geeks.info.gawla_app.Views.AccountOptions.BuyingProcessesActivity;
import it_geeks.info.gawla_app.Views.AccountOptions.PrivacyDetails;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.Views.MainActivity;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Views.NotificationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    TextView userName;
    CircleImageView userImage;
    ImageView edit_user_image, upload_user_image;
    int user_id;
    String name, image;
    String api_token;

    private UploadImageService uploadImageService;
    private UploadImageViewModel imageViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        user_id = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();

        getData();

        initViews(view);

        setImageService(view);

        setData();

        return view;
    }

    private void getData() {  /// get data from sharedPreference
        name = SharedPrefManager.getInstance(getContext()).getUser().getName();
        image = SharedPrefManager.getInstance(getContext()).getUserImage();
    }

    private void initViews(View v) {  //  initialize Views
        userName = v.findViewById(R.id.user_name);
        userImage = v.findViewById(R.id.user_image);
        edit_user_image = v.findViewById(R.id.edit_user_image);
        upload_user_image = v.findViewById(R.id.upload_user_image);

        // choose new image
        edit_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNewImage();
            }
        });

        //intent to account details
        v.findViewById(R.id.cv_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),AccountDetails.class));
            }
        });

        //intent to Privacy details
        v.findViewById(R.id.cv_privacy_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),PrivacyDetails.class));
            }
        });

        // open buying processes page
        v.findViewById(R.id.account_option_buying_processes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),BuyingProcessesActivity.class));
            }
        });

        // open Notification
        v.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),NotificationActivity.class));
            }
        });
    }

    private void setData() { // set data to views
        Picasso.with(getContext()).load(image).placeholder(R.mipmap.ic_launcher_gawla).into(userImage);
        userName.setText(name);
    }

    private void selectNewImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri imagePath = data.getData();

            // display image before uploading
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // transform image to bytes || string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageAsByte = outputStream.toByteArray();
            final String encodedImage = Base64.encodeToString(imageAsByte, Base64.DEFAULT);

            // upload image
            upload_user_image.setVisibility(View.VISIBLE);
            upload_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UploadImage(encodedImage);
                }
            });
        }
    }

    private void UploadImage(String encodedImage) {
        upload_user_image.setVisibility(View.INVISIBLE);

        final RequestMainBody requestMainBody = new RequestMainBody(new Data("updateUserData"), new Request(user_id, api_token, encodedImage));
        try {
            new AsyncUploadImage().execute(requestMainBody);
        } catch (RuntimeException e) {
        }
    }

    private class AsyncUploadImage extends AsyncTask<RequestMainBody, Void, String> {

        @Override
        protected String doInBackground(RequestMainBody... requestMainBody) {
            final String[] imageuploaded = new String[1];
            Call<JsonObject> call = RetrofitClient.getInstance(getContext()).getAPI().request(requestMainBody[0]);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                    try{
                        JsonObject ObjData = response.body().getAsJsonObject();
                        boolean status = ObjData.get("status").getAsBoolean();
                        if (status) {
                            JsonObject data = ObjData.get("userData").getAsJsonObject();
                            imageuploaded[0] = data.get("image").getAsString();
                            SharedPrefManager.getInstance(getActivity()).saveUserImage(image);
                            Toast.makeText(MainActivity.mainActivityInstance, "Your Profile Image has been changed", Toast.LENGTH_SHORT).show();
                            Picasso.with(getContext()).load(imageuploaded[0]).into(userImage);
                        } else {
                            if (handleServerErrors(ObjData).equals("you are not logged in.")) {
                                startActivity(new Intent(getActivity(), LoginActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                            Toast.makeText(MainActivity.mainActivityInstance, handleServerErrors(ObjData), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("Mo7",e.getMessage());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(MainActivity.mainActivityInstance, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            });
            return imageuploaded[0];
        }

        @Override
        protected void onPostExecute(String image) {
            super.onPostExecute(image);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    private void setImageService(View view) {
        imageViewModel = ViewModelProviders.of(this).get(UploadImageViewModel.class);

        imageViewModel.getBinder().observe(this, new Observer<UploadImageService.ImageBinder>() {
            @Override
            public void onChanged(@Nullable UploadImageService.ImageBinder imageBinder) {
                if (imageBinder != null) {
                    Log.d(TAG, "onChanged: connected to service");
                    uploadImageService = imageBinder.getService();

                } else {
                    Log.d(TAG, "onChanged: unbound from service");
                    uploadImageService = null;
                }
            }
        });

        imageViewModel.getIsProgressUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {

                } else {

                }
            }
        });
    }

    public void updates() {
        if (uploadImageService != null) {
            if (uploadImageService.isPaused()) {
                uploadImageService.unPauseTask();
                imageViewModel.setIsProgressUpdating(true);
            } else {
                uploadImageService.pauseTask();
                imageViewModel.setIsProgressUpdating(false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (imageViewModel.getBinder() != null) {
            getActivity().unbindService(imageViewModel.getServiceConnection());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startServices();
    }

    private void startServices() {
        Intent serviceIntent = new Intent(getContext(), UploadImageService.class);
        getActivity().startService(serviceIntent);

        bindService();
    }

    private void bindService() {
        Intent serviceIntent = new Intent(getContext(), UploadImageService.class);
        getActivity().bindService(serviceIntent, imageViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }
}
