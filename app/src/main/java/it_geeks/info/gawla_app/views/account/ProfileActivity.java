package it_geeks.info.gawla_app.views.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.util.services.UploadImageService;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;

import static it_geeks.info.gawla_app.util.Constants.REQ_UPDATE_USER_DATA;

public class ProfileActivity extends AppCompatActivity {

    public static ProfileActivity accountDetailsInstance;
    private EditText et_Email, et_name, et_telephone, sp_gender, sp_country;
    private ImageView ivUserImage, btn_choose_image;
    public ImageView btn_upload_image;
    private TextView btn_update_profile;
    private ProgressBar progressBarUpdateProfile;

    private int user_id;
    private String api_token;
    public String encodedImage;

    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_profile);
        accountDetailsInstance = this;

        user_id = SharedPrefManager.getInstance(ProfileActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(ProfileActivity.this).getUser().getApi_token();

        initViews();

        bindUserData();

        handleEvents();
    }

    private void initViews() {
        et_name = findViewById(R.id.et_update_first_name);
        et_telephone = findViewById(R.id.et_update_telephone);
        et_Email = findViewById(R.id.et_account_email);
        sp_country = findViewById(R.id.my_sp_country);
        sp_gender = findViewById(R.id.my_sp_gender);
        ivUserImage = findViewById(R.id.iv_account_Image);

        btn_choose_image = findViewById(R.id.btn_choose_image);
        btn_upload_image = findViewById(R.id.btn_upload_image);

        btn_update_profile = findViewById(R.id.btn_update_profile);
        progressBarUpdateProfile = findViewById(R.id.progress_update_profile);

        snackBuilder = new SnackBuilder(findViewById(R.id.profile_main_layout));

        et_name.requestFocus();

        initCountriesMaterialSpinner();

        initGenderMaterialSpinner();
    }

    private void handleEvents() {
        // save
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDataOnServer();
            }
        });

        // change image
        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initCountriesMaterialSpinner() {
        sp_country.setInputType(InputType.TYPE_NULL);
        sp_country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    countryPopupMenu();
                }
            }
        });
        sp_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPopupMenu();
            }
        });
    }

    private void initGenderMaterialSpinner() {
        sp_gender.setInputType(InputType.TYPE_NULL);
        sp_gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    genderPopupMenu();
                }
            }
        });
        sp_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderPopupMenu();
            }
        });
    }

    private void countryPopupMenu() {
        sp_country.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_up), null);

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
        PopupMenu countryPopup = new PopupMenu(wrapper, sp_country);

        List<String> countries = GawlaDataBse.getInstance(this).countryDao().getCountriesNames();
        for (String country : countries) {
            countryPopup.getMenu().add(country);
        }

        countryPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sp_country.setText(item.getTitle());
                return true;
            }
        });

        countryPopup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                sp_country.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down), null);
            }
        });

        try {
            countryPopup.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void genderPopupMenu() {
        sp_gender.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_up), null);

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
        PopupMenu genderPopup = new PopupMenu(wrapper, sp_gender);
        genderPopup.getMenuInflater().inflate(R.menu.gender_menu, genderPopup.getMenu());

        genderPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sp_gender.setText(item.getTitle());
                return true;
            }
        });

        genderPopup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                sp_gender.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down), null);
            }
        });

        try {
            genderPopup.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void bindUserData() {
        User user = SharedPrefManager.getInstance(ProfileActivity.this).getUser();

        ImageLoader.getInstance().load(user.getImage(), ivUserImage);
        et_name.setText(user.getName());
        et_Email.setText(user.getEmail());
        et_telephone.setText(user.getPhone());
        sp_gender.setText(user.getGender());
        sp_country.setText(GawlaDataBse.getInstance(ProfileActivity.this).countryDao().getCountryNameByID(user.getCountry_id()));

        if (sp_country.getText().toString().isEmpty())
            sp_country.setText(SharedPrefManager.getInstance(this).getCountry().getCountry_title());
    }

    private void updateUserDataOnServer() {
        try {
            setUIOnUpdating();
            final Country country = GawlaDataBse.getInstance(ProfileActivity.this).countryDao().getCountryByName(sp_country.getText().toString());
            RetrofitClient.getInstance(ProfileActivity.this)
                    .executeConnectionToServer(ProfileActivity.this,
                            REQ_UPDATE_USER_DATA, new Request<>(REQ_UPDATE_USER_DATA,
                                    user_id,
                                    api_token,
                                    et_name.getText().toString(),
                                    et_Email.getText().toString(),
                                    et_telephone.getText().toString(),
                                    sp_gender.getText().toString(),
                                    country.getCountry_id()), new HandleResponses() {
                                @Override
                                public void handleTrueResponse(JsonObject mainObject) {
                                    // unsubscribe remote notification from previous country
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + SharedPrefManager.getInstance(ProfileActivity.this).getCountry().getCountry_id());

                                    // save updated data locally
                                    SharedPrefManager.getInstance(ProfileActivity.this).saveUser(ParseResponses.parseUser(mainObject));
                                    SharedPrefManager.getInstance(ProfileActivity.this).setCountry(country);

                                    // subscribe remote notification to current country
                                    FirebaseMessaging.getInstance().subscribeToTopic("country_" + SharedPrefManager.getInstance(ProfileActivity.this).getCountry().getCountry_id());

                                    // notify user
                                    snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();
                                }

                                @Override
                                public void handleAfterResponse() {
                                    setUIAfterUpdating();
                                }

                                @Override
                                public void handleConnectionErrors(String errorMessage) {
                                    setUIAfterUpdating();
                                    snackBuilder.setSnackText(errorMessage).showSnack();
                                }
                            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void displayUploadImageButton() {
        btn_upload_image.animate().translationX(btn_upload_image.getWidth() + 10).setDuration(500).start();
        btn_upload_image.setEnabled(true);
    }

    public void hideUploadImageButton() {
        btn_upload_image.animate().translationX(0).setDuration(400).start();
        btn_upload_image.setEnabled(false);
    }

    public void setUIOnUpdating() {
        progressBarUpdateProfile.setVisibility(View.VISIBLE);
        btn_update_profile.setVisibility(View.INVISIBLE);

        et_name.setEnabled(false);
        et_Email.setEnabled(false);
        et_telephone.setEnabled(false);
        sp_country.setEnabled(false);
        sp_gender.setEnabled(false);
        btn_upload_image.setEnabled(false);
    }

    public void setUIAfterUpdating() {
        progressBarUpdateProfile.setVisibility(View.GONE);
        btn_update_profile.setVisibility(View.VISIBLE);

        et_name.setEnabled(true);
        et_Email.setEnabled(true);
        et_telephone.setEnabled(true);
        sp_country.setEnabled(true);
        sp_gender.setEnabled(true);
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
        RetrofitClient.getInstance(this).cancelCall();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                getImageUri(data);
            }
        }
    }

    private void getImageUri(Intent data) {
        Uri imagePath = data.getData();

        try {
            // display image before uploading
            ImageLoader.getInstance().load(imagePath.toString(), ivUserImage);

            // transform image to bytes || string
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), imagePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] imageAsByte = outputStream.toByteArray();
            encodedImage = Base64.encodeToString(imageAsByte, Base64.DEFAULT);

            // upload image
            displayUploadImageButton();
            btn_upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.Instance().isConnected(ProfileActivity.this)) {
                        startUploadImageService();
                        setUIOnUpdating();
                    } else {
                        snackBuilder.setSnackText(getString(R.string.no_connection)).showSnack();
                    }
                }
            });

        } catch (Exception e) {
            snackBuilder.setSnackText(e.getLocalizedMessage()).showSnack();
            Crashlytics.logException(e);
        }
    }

    private void startUploadImageService() {
        // upload image in a service
        startService(new Intent(getApplicationContext(), UploadImageService.class));
    }
}
