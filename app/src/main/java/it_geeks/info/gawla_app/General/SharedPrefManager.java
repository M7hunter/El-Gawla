package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "lang_shared_pref";

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
    public void Account_Save(String status){
        context.getSharedPreferences("AccountLogin", MODE_PRIVATE)
                .edit()
                .putString("status",status)
                .commit();
    }
    public String getAccount_Save(){
        SharedPreferences pref=context.getSharedPreferences("AccountLogin",MODE_PRIVATE);
        return pref.getString("status",null);
    }

    public void logout(){
        context.getSharedPreferences("AccountLogin", MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }
}

