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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.services.UploadImageService;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;

public class AccountDetailsActivity extends AppCompatActivity {

    public static AccountDetailsActivity accountDetailsInstance;
    private EditText et_update_first_name, et_update_last_name, et_update_telephone, sp_update_gender, sp_update_country;
    private ImageView ivUserImage, btn_choose_image;
    public ImageView btn_upload_image;
    private TextView btn_update_profile;
    private ProgressBar progressBarUpdateProfile;

    private int user_id;
    private String api_token;
    public String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_account_details);
        accountDetailsInstance = this;

        getCountriesFromSever();

        user_id = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser().getApi_token();

        initViews();

        bindUserData();

        handleEvents();
    }

    private void initViews() {
        et_update_first_name = findViewById(R.id.et_update_first_name);
        et_update_last_name = findViewById(R.id.et_update_last_name);
        et_update_telephone = findViewById(R.id.et_update_telephone);
        sp_update_country = findViewById(R.id.my_sp_country);
        sp_update_gender = findViewById(R.id.my_sp_gender);
        ivUserImage = findViewById(R.id.iv_user_Image);

        btn_choose_image = findViewById(R.id.btn_choose_image);
        btn_upload_image = findViewById(R.id.btn_upload_image);

        btn_update_profile = findViewById(R.id.btn_update_profile);
        progressBarUpdateProfile = findViewById(R.id.progress_update_profile);

        et_update_first_name.requestFocus();

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
        findViewById(R.id.account_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initCountriesMaterialSpinner() {
        sp_update_country.setInputType(InputType.TYPE_NULL);
        sp_update_country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    countryPopupMenu();
                }
            }
        });
        sp_update_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPopupMenu();
            }
        });
    }

    private void initGenderMaterialSpinner() {
        sp_update_gender.setInputType(InputType.TYPE_NULL);
        sp_update_gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    genderPopupMenu();
                }
            }
        });
        sp_update_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderPopupMenu();
            }
        });
    }

    private void countryPopupMenu() {
        sp_update_country.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_up), null);

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
        PopupMenu countryPopup = new PopupMenu(wrapper, sp_update_country);

        List<String> countries = GawlaDataBse.getInstance(this).countryDao().getCountriesNames();
        for (String country : countries) {
            countryPopup.getMenu().add(country);
        }

        countryPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sp_update_country.setText(item.getTitle());
                return true;
            }
        });

        countryPopup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                sp_update_country.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down), null);
            }
        });

        try {
            countryPopup.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void getCountriesFromSever() {
        final String apiToken = "8QEqV21eAUneQcZYUmtw7yXhlzXsUuOvr6iH2qg9IBxwzYSOfiGDcd0W8vme";
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                "getAllCountries", new Request(apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        GawlaDataBse.getInstance(AccountDetailsActivity.this).countryDao().insertCountryList(ParseResponses.parseCountries(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(AccountDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void genderPopupMenu() {
        sp_update_gender.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_up), null);

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuTheme);
        PopupMenu genderPopup = new PopupMenu(wrapper, sp_update_gender);
        genderPopup.getMenuInflater().inflate(R.menu.gender_menu, genderPopup.getMenu());

        genderPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sp_update_gender.setText(item.getTitle());
                return true;
            }
        });

        genderPopup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                sp_update_gender.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down), null);
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
        User user = SharedPrefManager.getInstance(AccountDetailsActivity.this).getUser();

        Log.d("image_url:", user.getImage());
        Picasso.with(this).load(user.getImage()).placeholder(R.drawable.placeholder).into(ivUserImage);
        et_update_first_name.setText(user.getFirstName());
        et_update_last_name.setText(user.getLastName());
        et_update_telephone.setText(user.getPhone());
        sp_update_gender.setText(user.getGender());
        sp_update_country.setText(GawlaDataBse.getInstance(AccountDetailsActivity.this).countryDao().getCountryNameByID(user.getCountry_id()));

        if (sp_update_country.getText().toString().isEmpty())
            sp_update_country.setText(SharedPrefManager.getInstance(this).getCountry().getCountry_title());
    }

    private void updateUserDataOnServer() {
        try {
            setUIOnUpdating();
            final Country country = GawlaDataBse.getInstance(AccountDetailsActivity.this).countryDao().getCountryByName(sp_update_country.getText().toString());
            RetrofitClient.getInstance(AccountDetailsActivity.this)
                    .executeConnectionToServer(AccountDetailsActivity.this,
                            "updateUserData",
                            new Request(user_id,
                                    api_token,
                                    et_update_first_name.getText().toString(),
                                    et_update_last_name.getText().toString(),
                                    et_update_telephone.getText().toString(),
                                    sp_update_gender.getText().toString(),
                                    country.getCountry_id()), new HandleResponses() {
                                @Override
                                public void handleTrueResponse(JsonObject mainObject) {
                                    // unsubscribe remote notification from previous country
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + SharedPrefManager.getInstance(AccountDetailsActivity.this).getCountry().getCountry_id());

                                    // save updated data locally
                                    SharedPrefManager.getInstance(AccountDetailsActivity.this).saveUser(ParseResponses.parseUser(mainObject));
                                    SharedPrefManager.getInstance(AccountDetailsActivity.this).setCountry(country);

                                    // subscribe remote notification to current country
                                    FirebaseMessaging.getInstance().subscribeToTopic("country_" + String.valueOf(SharedPrefManager.getInstance(AccountDetailsActivity.this).getCountry().getCountry_id()));

                                    // notify user
                                    Toast.makeText(AccountDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleAfterResponse() {
                                    setUIAfterUpdating();
                                }

                                @Override
                                public void handleConnectionErrors(String errorMessage) {
                                    setUIAfterUpdating();
                                    Toast.makeText(AccountDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

        et_update_first_name.setEnabled(false);
        et_update_last_name.setEnabled(false);
        et_update_telephone.setEnabled(false);
        sp_update_country.setEnabled(false);
        sp_update_gender.setEnabled(false);
        btn_upload_image.setEnabled(false);
    }

    public void setUIAfterUpdating() {
        progressBarUpdateProfile.setVisibility(View.GONE);
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
            try {
                Picasso.with(this)
                        .load(imagePath)
                        .into(ivUserImage);
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }

            // transform image to bytes || string
//            ivUserImage.buildDrawingCache();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(AccountDetailsActivity.this.getContentResolver(), imagePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] imageAsByte = outputStream.toByteArray();
            encodedImage = Base64.encodeToString(imageAsByte, Base64.DEFAULT);

            // upload image
            displayUploadImageButton();
            btn_upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.Instance(AccountDetailsActivity.this).isConnected()) {
                        startUploadImageService();
                        setUIOnUpdating();
                    } else {
                        Toast.makeText(AccountDetailsActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(AccountDetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
        }
    }

    private void startUploadImageService() {
        // upload image in a service
        startService(new Intent(getApplicationContext(), UploadImageService.class));
    }
}
