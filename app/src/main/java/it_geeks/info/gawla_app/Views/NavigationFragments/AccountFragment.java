package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Views.AccountOptions.AccountDetails;
import it_geeks.info.gawla_app.Views.AccountOptions.BuyingProcessesActivity;
import it_geeks.info.gawla_app.Views.AccountOptions.PrivacyDetails;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Views.NotificationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    TextView userName;
    CircleImageView userImage;
    ImageView edit_user_image, upload_user_image;
    int user_id;
    String name, image;
    String api_token;

    ProgressBar imageProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        user_id = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();

        getData();

        initViews(view);

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
        imageProgress = v.findViewById(R.id.image_progress);

        // choose new image
        edit_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //intent to account details
        v.findViewById(R.id.cv_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountDetails.class));
            }
        });

        //intent to Privacy details
        v.findViewById(R.id.cv_privacy_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyDetails.class));
            }
        });

        // open buying processes page
        v.findViewById(R.id.account_option_buying_processes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BuyingProcessesActivity.class));
            }
        });

        // open Notification
        v.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void setData() { // set data to views
        Picasso.with(getContext()).load(image).placeholder(R.mipmap.ic_launcher_gawla).into(userImage);
        userName.setText(name);
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {

                final Uri imagePath = data.getData();

                try {
                    // display image before uploading
                    Picasso.with(getActivity()).load(imagePath).into(userImage);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagePath);

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
                            upload_user_image.setVisibility(View.INVISIBLE);
                            edit_user_image.setVisibility(View.INVISIBLE);
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void UploadImage(final String encodedImage) {
        imageProgress.setVisibility(View.VISIBLE);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                RetrofitClient.getInstance(getActivity()).getAPI().request(new RequestMainBody(new Data("updateUserData"), new Request(user_id, api_token, encodedImage)))
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    JsonObject mainObj = response.body().getAsJsonObject();
                                    boolean status = mainObj.get("status").getAsBoolean();
                                    String message = mainObj.get("message").getAsString();

                                    if (status) { // no errors
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    } else { // errors from server
                                        Toast.makeText(getActivity(), handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (NullPointerException e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                imageProgress.setVisibility(View.GONE);
                                edit_user_image.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                imageProgress.setVisibility(View.GONE);
                                edit_user_image.setVisibility(View.VISIBLE);
                                upload_user_image.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
//        }).start();
//    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }
}


