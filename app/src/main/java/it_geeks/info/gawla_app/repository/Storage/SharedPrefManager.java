package it_geeks.info.gawla_app.repository.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.util.Constants;

import static android.content.Context.MODE_PRIVATE;
import static it_geeks.info.gawla_app.util.Constants.NULL_INT_VALUE;

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
    private static final String SHARED_PREF_SOUND = "sound_shared_pref";
    private static final String SHARED_PREF_NEW_NOTIFICATION = "new_notification";
    private static final String SHARED_PREF_SUBSCRIBED_SALON = "subscribed_salon";
    private static final String SHARED_PREF_PAY_METHOD = "payment_method";
    private static final String SHARED_PREF_FIRE_TOKEN = "firebase_token";
    private static final String SHARED_PREF_USER_OFFER = "user_offer";

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    // region firebase token
    public void setFirebaseToken(String token) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FIRE_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("fire_token", token);
        editor.apply();
    }

    public String getFirebaseToken() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FIRE_TOKEN, MODE_PRIVATE);
        return sharedPreferences.getString("fire_token", Constants.EMPTY_TOKEN);
    }
    // endregion

    // region notification
    public void setNotificationEnabled(boolean enabled) { // enabled, disabled
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NOTIFICATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putBoolean("notification_enabled", enabled);
        editor.apply();
    }

    public boolean isNotificationEnabled() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NOTIFICATION, MODE_PRIVATE);
        return sharedPreferences.getBoolean("notification_enabled", true);
    }

    // New Notification status
    public void setNewNotification(Boolean newNotification) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putBoolean("new_notification", newNotification);
        editor.apply();
    }

    public boolean getNewNotification() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("new_notification", true);
    }

    public void clearNewNotification() {
        context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion

    // region sound
    public void setSoundEnabled(boolean enabled) { // enabled, disabled
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SOUND, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putBoolean("sound_enabled", enabled);
        editor.apply();
    }

    public boolean isSoundEnabled() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SOUND, MODE_PRIVATE);
        return sharedPreferences.getBoolean("sound_enabled", true);
    }
    // endregion

    // region payment method
    public void setLastMethod(String method) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_PAY_METHOD, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("method", method);
        editor.apply();
    }

    public String getLastMethod() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_PAY_METHOD, MODE_PRIVATE);
        return sharedPreferences.getString("method", Constants.GOOGLEPAY);
    }
    // endregion

    // region lang
    public void setLang(String lang) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putString("lang", lang);
        editor.putBoolean("langChanged", true);
        editor.apply();
    }

    public boolean isLangChanged() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        boolean changed = sharedPreferences.getBoolean("langChanged", false);
        sharedPreferences.edit().putBoolean("langChanged", false).apply();
        return changed;
    }

    public String getSavedLang() {
        String defaultLang =  Locale.getDefault().getLanguage();
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        return sharedPreferences.getString("lang", defaultLang != null ? defaultLang : "ar"); // if null return device default language
    }
    // endregion

    // region user
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
                sharedPreferences.getInt("userId", NULL_INT_VALUE),
                sharedPreferences.getString("userToken", null),
                sharedPreferences.getString("userName", null),
                sharedPreferences.getInt("country_id", NULL_INT_VALUE),
                sharedPreferences.getString("userImage", null),
                sharedPreferences.getString("userEmail", null),
                sharedPreferences.getString("membership", null),
                sharedPreferences.getString("gender", null),
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
    // endregion

    // region country
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
        return new Country(sharedPreferences.getInt("country_id", -111),
                sharedPreferences.getString("country_title", ""),
                sharedPreferences.getString("count_code", ""),
                sharedPreferences.getString("country_timezone", ""),
                sharedPreferences.getString("tel", ""),
                sharedPreferences.getString("image", ""));
    }
    // endregion

    // region last request
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
    // endregion

    // region Social Provider
    public void saveProvider(String provider) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString("provider", provider);
        editor.apply();
    }

    public String getProvider() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("provider", null);
    }

    public void clearProvider() {
        context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion

    // region user offer
    public void saveUserOffer(String offerKey, String offerValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_OFFER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString(offerKey, offerValue);
        editor.apply();
    }

    public String getUserOffer(String offerKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_OFFER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(offerKey, "");
    }

    public void clearUserOffer(String USER_OFFER_KEY) {
        context.getSharedPreferences(USER_OFFER_KEY, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion

    // region subscribed salon id
    public void saveSubscribedSalonId(int salonId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putInt("subscribed_salon", salonId);
        editor.apply();
    }

    public int getSubscribedSalonId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("subscribed_salon", -111);
    }

    public void clearSubscribedSalonId() {
        context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion
}