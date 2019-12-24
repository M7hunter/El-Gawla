package it_geeks.info.elgawla.repository.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

import androidx.lifecycle.MutableLiveData;
import it_geeks.info.elgawla.repository.Models.Country;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.util.Constants;

import static android.content.Context.MODE_PRIVATE;
import static it_geeks.info.elgawla.util.Constants.NULL_INT_VALUE;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private MutableLiveData<Boolean> newNotificationLive = new MutableLiveData<>();
    private MutableLiveData<String> userSubscriptionLive = new MutableLiveData<>();

    private static final String SHARED_PREF_LANG = "lang_shared_pref";
    private static final String SHARED_PREF_USER = "user_shared_pref";
    private static final String SHARED_PREF_TOUR = "tour";
    private static final String SHARED_PREF_COUNTRY = "country_shared_pref";
    private static final String SHARED_PREF_SUBSCRIPTION = "subscription_shared_pref";
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
        if (sharedPrefManager == null)
        {
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
    public void setHaveNewNotification(Boolean newNotification) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        newNotificationLive.postValue(newNotification);
        editor.putBoolean("new_notification", newNotification);
        editor.apply();
    }

    public MutableLiveData<Boolean> haveNewNotification() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NEW_NOTIFICATION, Context.MODE_PRIVATE);
        newNotificationLive.postValue(sharedPreferences.getBoolean("new_notification", false));
        return newNotificationLive;
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
        return sharedPreferences.getString("method", Constants.KNET);
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

    public String getSavedLang() {
        String defaultLang = Locale.getDefault().getLanguage();
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        return sharedPreferences.getString("lang", defaultLang); // if null return device default language
    }

    public boolean isLangChanged() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_LANG, MODE_PRIVATE);
        boolean changed = sharedPreferences.getBoolean("langChanged", false);
        sharedPreferences.edit().putBoolean("langChanged", false).apply();
        return changed;
    }
    // endregion

    // region tour
    public void setSalonPageTourFinished(boolean isFinished) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_TOUR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("salon_page__tour_finished", isFinished);
        editor.apply();
    }

    public void setMainPageTourFinished(boolean isFinished) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_TOUR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("main_page_tour_finished", isFinished);
        editor.apply();
    }

    public boolean isSalonPageTourFinished() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_TOUR, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("salon_page__tour_finished", false);
    }

    public boolean isMainPageTourFinished() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_TOUR, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("main_page_tour_finished", false);
    }
    // endregion

    // region user
    public void saveUser(User user) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putInt("userId", user.getUser_id());
        editor.putString("userToken", user.getApi_token());
        editor.putString("userName", user.getName());
        editor.putInt("country_id", user.getCountry_id());
        editor.putString("userImage", user.getImage());
        editor.putString("userEmail", user.getEmail());
        editor.putBoolean("userActive", user.isActive());
        editor.putString("gender", user.getGender());
        editor.putString("phone", user.getPhone());

        editor.putBoolean("userLogged", true);

        editor.apply();
    }

    public boolean isLoggedIn() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("userLogged", false);
    }

    public User getUser() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);

        return new User(
                sharedPreferences.getInt("userId", NULL_INT_VALUE),
                sharedPreferences.getString("userToken", null),
                sharedPreferences.getString("userName", null),
                sharedPreferences.getInt("country_id", NULL_INT_VALUE),
                sharedPreferences.getString("userImage", null),
                sharedPreferences.getString("userEmail", null),
                sharedPreferences.getBoolean("userActive", false),
                sharedPreferences.getString("gender", null),
                sharedPreferences.getString("phone", null)
        );
    }

    public void clearUser() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
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

        if (country != null)
        {
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

    // region subscription
    public void setUserSubscription(String subscription) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIPTION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        userSubscriptionLive.postValue(subscription);
        editor.putString("subscription", subscription);
        editor.apply();
    }

    public MutableLiveData<String> getUserSubscription() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIPTION, MODE_PRIVATE);
        userSubscriptionLive.postValue(sharedPreferences.getString("subscription", "0"));
        return userSubscriptionLive;
    }
    // endregion

    // region Social Provider
    public void saveProvider(String provider) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString("provider", provider);
        editor.apply();
    }

    public String getProvider() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("provider", null);
    }

    public void clearProvider() {
        context.getSharedPreferences(SHARED_PREF_USER_PROVIDER, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion

    // region user offer
    public void saveUserOffer(String offerKey, String offerValue) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_OFFER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString(offerKey, offerValue);
        editor.apply();
    }

    public String getUserOffer(String offerKey) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER_OFFER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(offerKey, "0");
    }

    public void clearUserOffer(String USER_OFFER_KEY) {
        context.getSharedPreferences(USER_OFFER_KEY, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion

    // region subscribed salon id
    public void saveSubscribedSalonId(int salonId) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putInt("subscribed_salon", salonId);
        editor.apply();
    }

    public int getSubscribedSalonId() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("subscribed_salon", -111);
    }

    public void clearSubscribedSalonId() {
        context.getSharedPreferences(SHARED_PREF_SUBSCRIBED_SALON, Context.MODE_PRIVATE).edit().clear().apply();
    }
    // endregion
}