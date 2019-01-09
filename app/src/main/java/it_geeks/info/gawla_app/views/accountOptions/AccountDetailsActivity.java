package it_geeks.info.gawla_app.views.accountOptions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;

public class AccountDetailsActivity extends AppCompatActivity {

    EditText ed_update_first_name , ed_update_second_name , ed_update_telephone;
    Spinner sp_update_country , sp_update_gender;
    ImageView img_update_image , edit_update_image;
    TextView btn_update_profile;
    int user_id;
    String api_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_account_details);

        initViews();

        getData();

    }

    private void initViews() {
        // back
        findViewById(R.id.account_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ed_update_first_name = findViewById(R.id.ed_update_first_name);
        ed_update_second_name = findViewById(R.id.ed_update_second_name);
        ed_update_telephone = findViewById(R.id.ed_update_telephone);
        sp_update_country = findViewById(R.id.sp_update_country);
        sp_update_gender = findViewById(R.id.sp_update_gender);
        img_update_image = findViewById(R.id.img_update_Image);
        edit_update_image = findViewById(R.id.edit_update_image);
        btn_update_profile = findViewById(R.id.btn_update_profile);

        edit_update_image.setOnClickListener(onClickListener);
        btn_update_profile.setOnClickListener(onClickListener);
    }

    private void getData() {

        try {
            Picasso.with(AccountDetailsActivity.this)
                    .load(SharedPrefManager.getInstance(AccountDetailsActivity.this).getUserImage())
                    .placeholder(AccountDetailsActivity.this.getResources().getDrawable(R.drawable.placeholder))
                    .into(img_update_image);
        }catch (Exception e){}

    }

    private View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.edit_update_image:  // clicked to choose image
                    selectImage();
                    break;
                case R.id.btn_update_profile:
                    //    saveProfileData();
                    break;

            } // end of switch
        }
    };

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
                getImageUri(data);
            }
        }
    }

    private void getImageUri(Intent data) {

        final Uri imagePath = data.getData();

        try {
            // display image before uploading
            Picasso.with(AccountDetailsActivity.this).load(imagePath).into(img_update_image);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(AccountDetailsActivity.this.getContentResolver(), imagePath);

            // transform image to bytes || string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageAsByte = outputStream.toByteArray();
            final String encodedImage = Base64.encodeToString(imageAsByte, Base64.DEFAULT);

            // upload image
            UploadImage(encodedImage);


        } catch (Exception e) {
            Toast.makeText(AccountDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void UploadImage(final String encodedImage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                user_id = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getUser_id();
                api_token = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getApi_token();
                RetrofitClient.getInstance(AccountDetailsActivity.this).executeConnectionToServer("updateUserData", new Request(user_id, api_token, encodedImage), new HandleResponses() {
                    @Override
                    public void handleResponseData(JsonObject mainObject) {

                        ParseResponses.parseCountries(mainObject);

//                        SharedPrefManager.getInstance(AccountDetailsActivity.this).saveUserImage(image); //

                        Picasso.with(AccountDetailsActivity.this)
                                .load("asd")
                                .placeholder(AccountDetailsActivity.this.getResources().getDrawable(R.drawable.placeholder))
                                .into(img_update_image);
                    }

                    @Override
                    public void handleEmptyResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {

                        Toast.makeText(AccountDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
