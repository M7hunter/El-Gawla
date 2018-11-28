package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.SharedPreferences;

import it_geeks.info.gawla_app.Models.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "lang_shared_pref";
    private static final String SHARED_PREF_NAME2 = "user_shared_pref";

    public SharedPrefManager(Context context) {
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

        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.putString("userPass", user.getPassword());
        editor.putBoolean("userLogged", true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("userLogged", false);
    }

    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);

        return new User(
                sharedPreferences.getString("userName", null),
                sharedPreferences.getString("userEmail", null),
                sharedPreferences.getString("userPass", null)
        );
    }

    public void clearUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLogged", false);
        editor.clear();
        editor.apply();
    }


        public void Country_Save(String country){
        context.getSharedPreferences("SaveCountry", MODE_PRIVATE)
                .edit()
                .putString("splash",country)
                .commit();
    }
    public String getCountry_Save(){
        SharedPreferences pref=context.getSharedPreferences("SaveCountry",MODE_PRIVATE);
        return pref.getString("splash",null);
    }
    public void Intro_Save(String intro){
        context.getSharedPreferences("SaveIntro", MODE_PRIVATE)
                .edit()
                .putString("intro",intro)
                .commit();
    }
    public String getIntro_Save(){
        SharedPreferences pref=context.getSharedPreferences("SaveIntro",MODE_PRIVATE);
        return pref.getString("intro",null);
    }
    public void Account_Save(boolean status,String api_token,String user_id){
        context.getSharedPreferences("AccountLogin", MODE_PRIVATE)
                .edit()
                .putBoolean("status",status)
                .putString("user_id",user_id)
                .putString("api_token",api_token)
                .commit();
    }
    public SharedPreferences getAccount_Save(){
        SharedPreferences pref=context.getSharedPreferences("AccountLogin",MODE_PRIVATE);
        return pref;
    }

    public void logout(){
        context.getSharedPreferences("AccountLogin", MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }
}

