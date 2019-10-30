package it_geeks.info.elgawla.views.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.views.intro.SplashScreenActivity;

public class LanguageActivity extends AppCompatActivity {

    private DialogBuilder dialogBuilder;

    private Button btnEnglish, btnArabic;
    private ImageView ivSAFlag, ivUSFlag, ivSACheck, ivUSCheck;

    private String selectedLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        selectedLang = SharedPrefManager.getInstance(this).getSavedLang();

        initViews();

        handleEvents();
    }

    private void initViews() {
        btnArabic = findViewById(R.id.btn_splash_arabic);
        btnEnglish = findViewById(R.id.btn_splash_english);
        ivSAFlag = findViewById(R.id.sa_flag);
        ivUSFlag = findViewById(R.id.us_flag);
        ivSACheck = findViewById(R.id.sa_check);
        ivUSCheck = findViewById(R.id.us_check);

        if ("ar".equals(selectedLang))
        {
            selectArabic();
        } else
        {
            selectEnglish();
        }

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createAlertDialog(this, new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("selected_lang", "::: " + selectedLang);
                        SharedPrefManager.getInstance(LanguageActivity.this).setLang(selectedLang);
                        restartTheApp();
                    }
                }, 1000);
            }

            @Override
            public void onNegativeCLick() {

            }
        });
        dialogBuilder.setAlertText(getResources().getString(R.string.restart_hint));
    }

    private void handleEvents() {
        btnArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"ar".equals(SharedPrefManager.getInstance(LanguageActivity.this).getSavedLang()))
                {
                    selectedLang = "ar";
                    dialogBuilder.displayAlertDialog();
                }
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"en".equals(SharedPrefManager.getInstance(LanguageActivity.this).getSavedLang()))
                {
                    selectedLang = "en";
                    dialogBuilder.displayAlertDialog();
                }
            }
        });

        ivSAFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnArabic.performClick();
            }
        });

        ivUSFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnglish.performClick();
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

    private void selectArabic() {
        ivSACheck.setVisibility(View.VISIBLE);
        ivUSCheck.setVisibility(View.GONE);
        btnArabic.setTextColor(getResources().getColor(R.color.colorSecondary));
        btnEnglish.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectEnglish() {
        ivSACheck.setVisibility(View.GONE);
        ivUSCheck.setVisibility(View.VISIBLE);
        btnArabic.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnEnglish.setTextColor(getResources().getColor(R.color.colorSecondary));
    }

    private void restartTheApp() {
        Intent i = new Intent(this, SplashScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
