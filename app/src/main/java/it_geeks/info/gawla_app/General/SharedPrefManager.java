package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.SharedPreferences;

import it_geeks.info.gawla_app.Repositry.Models.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "lang_shared_pref";
    private static final String SHARED_PREF_NAME2 = "user_shared_pref";
    private static final String SHARED_PREF_NAME3 = "country_shared_pref";
    private static final String SHARED_PREF_USER_IMAGE = "user_image_shared_pref";

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    //--------------- lang -------------//
    public void setLang(String lang) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("lang", lang);
        editor.apply();
    }

    public String getSavedLang() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("lang", "en");
    }

    //--------------- user -------------//
    public void saveUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putInt("userId", user.getUser_id());
        editor.putString("userName", user.getName());
        editor.putBoolean("userActive", user.isActive());
        editor.putString("userToken", user.getApi_token());
        editor.putString("userImage", user.getImage());
        editor.putString("userEmail", user.getEmail());

        editor.putBoolean("userLogged", true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("userLogged", false);
    }

    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);

        return new User(
                sharedPreferences.getInt("userId", -1),
                sharedPreferences.getString("userName", null),
                sharedPreferences.getString("userEmail", null),
                sharedPreferences.getBoolean("userActive", false),
                sharedPreferences.getString("userToken", null),
                sharedPreferences.getString("userImage", null)
        );
    }

    public void clearUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLogged", false);
        editor.clear();
        editor.apply();
    }

    //--------------- country -------------//
    public void setCountryId(int countryId) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME3, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putInt("countryId", countryId);
        editor.apply();
    }

    public int getCountryId() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME3, MODE_PRIVATE);
        return sharedPreferences.getInt("countryId", 0);
    }

    // ----  user profile ---- //
    public void saveUserImage(String image) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_IMAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString("userImage", image);
        editor.apply();
    }

    public String getUserImage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_IMAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userImage",null);
    }

    // ----  member ship ---- //
    public void setMembership(String membership) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME3, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("membership", membership);
        editor.apply();
    }

    public int getMembership() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME3, MODE_PRIVATE);
        return sharedPreferences.getInt("membership", 0);
    }
}

