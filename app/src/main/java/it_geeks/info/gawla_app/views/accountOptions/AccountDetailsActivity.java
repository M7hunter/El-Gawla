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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import it_geeks.info.gawla_app.Controllers.Adapters.CountrySpinnerAdapter;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.General.UploadStatus;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.NavigationFragments.AccountFragment;

public class AccountDetailsActivity extends AppCompatActivity {

    EditText et_update_first_name, et_update_last_name, et_update_telephone;
    Spinner sp_update_country, sp_update_gender;
    ImageView img_update_image, btn_choose_image, btn_upload_image;
    TextView btn_update_profile;
    int user_id;
    String api_token;

    Uri imagePath;

    ProgressBar progressBarUpdateProfile;
    Thread uploadImageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_account_details);

        user_id = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getApi_token();

        initViews();

        setUserData();
    }

    @Override
    protected void onResume() {
        if (SharedPrefManager.getInstance(AccountDetailsActivity.this).getUploadStatus().equals(UploadStatus.UPLOADING.toString())) {
            updateUI();
            Picasso.with(this).load(imagePath).placeholder(R.drawable.placeholder).into(img_update_image);
        }

        super.onResume();
    }

    private void initViews() {
        et_update_first_name = findViewById(R.id.et_update_first_name);
        et_update_last_name = findViewById(R.id.et_update_last_name);
        et_update_telephone = findViewById(R.id.et_update_telephone);
        sp_update_country = findViewById(R.id.sp_update_country);
        sp_update_gender = findViewById(R.id.sp_update_gender);
        img_update_image = findViewById(R.id.img_update_Image);

        btn_choose_image = findViewById(R.id.btn_choose_image);
        btn_upload_image = findViewById(R.id.btn_upload_image);

        btn_update_profile = findViewById(R.id.btn_update_profile);
        progressBarUpdateProfile = findViewById(R.id.progress_update_profile);

        sp_update_country.setAdapter(new CountrySpinnerAdapter(this, ((ArrayList<Country>) GawlaDataBse.getGawlaDatabase(this).countryDao().getCountries())));

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
                updateUserOnServer();
            }
        });

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // back
        findViewById(R.id.account_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUserData() {
        User user = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser();
        try {
            Picasso.with(AccountDetailsActivity.this)
                    .load(user.getImage())
                    .placeholder(AccountDetailsActivity.this.getResources().getDrawable(R.drawable.placeholder))
                    .into(img_update_image);
        } catch (Exception e) {
        }

        et_update_first_name.setText(user.getFirstName());
        et_update_last_name.setText(user.getLastName());
        et_update_telephone.setText(user.getPhone());
        sp_update_country.setSelection(user.getCountry_id());
        sp_update_gender.setSelection(genderId(user.getGender()));
    }

    private void updateUserOnServer() {
        RetrofitClient.getInstance(AccountDetailsActivity.this)
                .executeConnectionToServer("updateUserData",
                        new Request(user_id,
                                api_token,
                                et_update_first_name.getText().toString(),
                                et_update_last_name.getText().toString(),
                                et_update_telephone.getText().toString(),
                                genderLabel(sp_update_gender.getSelectedItemPosition()),
                                ((Country) sp_update_country.getSelectedItem()).getCountry_id()), new HandleResponses() {
                            @Override
                            public void handleResponseData(JsonObject mainObject) {

                                // save updated user data
                                SharedPrefManager.getInstance(AccountDetailsActivity.this).saveUser(ParseResponses.parseUser(mainObject));

                                // notify user
                                Toast.makeText(AccountDetailsActivity.this, "updated", Toast.LENGTH_SHORT).show();
                                updatedUI();
                            }

                            @Override
                            public void handleEmptyResponse() {
                            }

                            @Override
                            public void handleConnectionErrors(String errorMessage) {
                                Toast.makeText(AccountDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                updatedUI();
                            }
                        });
    }

    private void displayUploadImageButton() {
        btn_upload_image.animate().translationX(btn_upload_image.getWidth() + 10).setDuration(500).start();
        btn_upload_image.setEnabled(true);
    }

    private String genderLabel(int position) {
        switch (position) {
            case 0:
                return "Male";
            case 1:
                return "Female";
        }
        return "Male";
    }

    private int genderId(String gender) {
        switch (gender) {
            case "Male":
                return 0;
            case "Female":
                return 1;
        }
        return 0;
    }

    private void hideUploadImageButton() {
        btn_upload_image.animate().translationX(0).setDuration(400).start();
        btn_upload_image.setEnabled(false);
    }

    private void updateUI() {
        progressBarUpdateProfile.setVisibility(View.VISIBLE);
        btn_update_profile.setVisibility(View.INVISIBLE);

        et_update_first_name.setEnabled(false);
        et_update_last_name.setEnabled(false);
        et_update_telephone.setEnabled(false);
        sp_update_country.setEnabled(false);
        sp_update_gender.setEnabled(false);
    }

    private void updatedUI() {
        progressBarUpdateProfile.setVisibility(View.INVISIBLE);
        btn_update_profile.setVisibility(View.VISIBLE);

        et_update_first_name.setEnabled(true);
        et_update_last_name.setEnabled(true);
        et_update_telephone.setEnabled(true);
        sp_update_country.setEnabled(true);
        sp_update_gender.setEnabled(true);
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
                getImageUri(data);
            }
        }
    }

    private void getImageUri(Intent data) {

        imagePath = data.getData();

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
            displayUploadImageButton();
            btn_upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUI();
                    btn_upload_image.setEnabled(false);
                    uploadImage(encodedImage);
                }
            });

        } catch (Exception e) {
            Toast.makeText(AccountDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(final String encodedImage) {
        uploadImageThread = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPrefManager.getInstance(AccountDetailsActivity.this).setUploadStatus(UploadStatus.UPLOADING);

                RetrofitClient.getInstance(AccountDetailsActivity.this).executeConnectionToServer("updateUserData", new Request(user_id, api_token, encodedImage), new HandleResponses() {
                    @Override
                    public void handleResponseData(JsonObject mainObject) {

                        // update uploading status
                        SharedPrefManager.getInstance(AccountDetailsActivity.this).setUploadStatus(UploadStatus.UPLOADED);

                        User user = ParseResponses.parseUser(mainObject);

                        // display image to user after updating
                        Picasso.with(AccountDetailsActivity.this)
                                .load(user.getImage())
                                .placeholder(AccountDetailsActivity.this.getResources().getDrawable(R.drawable.placeholder))
                                .into(img_update_image);

//                        Picasso.with(AccountDetailsActivity.this)
//                                .load(user.getImage())
//                                .placeholder(AccountDetailsActivity.this.getResources().getDrawable(R.drawable.placeholder))
//                                .into(new AccountFragment().userImage);

                        // save updated user data
                        SharedPrefManager.getInstance(AccountDetailsActivity.this).saveUser(user);

                        // notify user
                        Toast.makeText(AccountDetailsActivity.this, "updated", Toast.LENGTH_SHORT).show();
                        updatedUI();
                        hideUploadImageButton();
                    }

                    @Override
                    public void handleEmptyResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(AccountDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        updatedUI();
                        btn_upload_image.setEnabled(true);
                        SharedPrefManager.getInstance(AccountDetailsActivity.this).setUploadStatus(UploadStatus.FAILED);
                    }
                });
            }
        });

        if (!SharedPrefManager.getInstance(AccountDetailsActivity.this).getUploadStatus().equals(UploadStatus.UPLOADING.toString())) {
            uploadImageThread.start();
        }
    }
}