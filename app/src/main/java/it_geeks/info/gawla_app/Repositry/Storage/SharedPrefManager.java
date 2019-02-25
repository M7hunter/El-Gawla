package it_geeks.info.gawla_app.Repositry.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {


    private static SharedPrefManager sharedPrefManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_LANG = "lang_shared_pref";
    private static final String SHARED_PREF_USER = "user_shared_pref";
    private static final String SHARED_PREF_COUNTRY = "country_shared_pref";
    private static final String SHARED_PREF_LAST_REQUEST = "last_request_shared_pref";
    private static final String SHARED_PREF_USER_PROVIDER = "user_socialMedia_Provider";
    private static final String SHARED_PREF_NOTIFICATION = "notification_shared_pref";
    private static final String SHARED_PREF_NEW_NOTIFICATION = "new_notification";

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    //--------------- notification -------------//
    public void setNotificationState(boolean state) { // enabled, disabled
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NOTIFICATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putBoolean("notification_enabled",  state);
        editor.apply();
    }

    public boolean getNotificationState() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NOTIFICATION, MODE_PRIVATE);
        return sharedPreferences.getBoolean("notification_enabled", false);
    }

    //--------------- lang -------------//
    public void setLang(String lang) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("lang", lang);
        editor.apply();
    }

    public String getSavedLang() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        return sharedPreferences.getString("lang", Locale.getDefault().getLanguage());
    }

    //--------------- user -------------//
    public void saveUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putInt("userId", user.getUser_id());
        editor.putString("userToken", user.getApi_token());
        editor.putString("userName", user.getName());
        editor.putInt("country_id", user.getCountry_id());
        editor.putString("userImage", user.getImage());
        editor.putString("userEmail", user.getEmail());
        editor.putString("membership", user.getMembership());
        editor.putString("gender", user.getGender());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("phone", user.getPhone());

        editor.putBoolean("userLogged", true);

        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("userLogged", false);
    }

    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);

        return new User(
                sharedPreferences.getInt("userId", -1),
                sharedPreferences.getString("userToken", null),
                sharedPreferences.getString("userName", null),
                sharedPreferences.getInt("country_id", -1),
                sharedPreferences.getString("userImage", null),
                sharedPreferences.getString("userEmail", null),
                sharedPreferences.getString("membership", null),
                sharedPreferences.getString("gender", null),
                sharedPreferences.getString("firstName", null),
                sharedPreferences.getString("lastName", null),
                sharedPreferences.getString("phone", null)
        );
    }

    public void clearUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLogged", false);
        editor.clear();
        editor.apply();
    }

    //--------------- country -------------//
    public void setCountry(Country country) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_COUNTRY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        if (country != null) {
        editor.putInt("country_id", country.getCountry_id());
        editor.putString("country_title", country.getCountry_title());
        editor.putString("count_code", country.getCount_code());
        editor.putString("country_timezone", country.getCountry_timezone());
        editor.putString("tel", country.getTel());
        editor.putString("image", country.getImage());
        editor.apply();
        }
    }

    public Country getCountry() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_COUNTRY, MODE_PRIVATE);
        return new Country(sharedPreferences.getInt("country_id", -1),
                sharedPreferences.getString("country_title", ""),
                sharedPreferences.getString("count_code", ""),
                sharedPreferences.getString("country_timezone", ""),
                sharedPreferences.getString("tel", ""),
                sharedPreferences.getString("image", ""));
    }

    //--------------- last request -------------//
    public void setLastRequest(long lastRequest) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LAST_REQUEST, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putLong("lastRequest", lastRequest);
        editor.apply();
    }

    public long getLastRequest() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LAST_REQUEST, MODE_PRIVATE);
        return sharedPreferences.getLong("lastRequest", 0);
    }

    // -------------- Social Provider ------------- //
    public void saveProvider(String provider) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString("provider", provider);
        editor.apply();
    }

    public String getProvider() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("provider",null);
    }

    public void clearProvider() {
       context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE).edit().clear().apply();
    }

    // New Notifications status
    public void setNewNotfication(Boolean newNotfication) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        
        editor.putBoolean("new_notification", newNotfication);
        editor.apply();
    }

    public boolean getNewNotification() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("new_notification",true);
    }

    public void clearNewNotification() {
        context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE).edit().clear().apply();
    }

    // Save User Offer
    public void saveUserOffer(String USER_OFFER_KEY ,int offerValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_OFFER_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putInt("user_offer", offerValue);
        editor.apply();
    }

    public int getUserOffer(String USER_OFFER_KEY) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_OFFER_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_offer",0);
    }

    public void clearUserOffer(String USER_OFFER_KEY) {
        context.getSharedPreferences(USER_OFFER_KEY, Context.MODE_PRIVATE).edit().clear().apply();
    }

}