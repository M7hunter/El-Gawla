package it_geeks.info.gawla_app.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.intro.SplashScreenActivity;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private Context context;
    private List<String> langList;
    private DialogBuilder dialogBuilder;

    public LanguageAdapter(Context context, List<String> langList) {
        this.context = context;
        this.langList = langList;
        dialogBuilder = new DialogBuilder();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String lang = langList.get(i);

        viewHolder.langLabel.setText(lang);

        if (sLang(lang).equals(SharedPrefManager.getInstance(context).getSavedLang()))
        {
            viewHolder.langLabel.setBackground(context.getDrawable(R.drawable.bg_rounded_c_white_bordered_c_primary));
        }

        // events
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sLang(lang).equals(SharedPrefManager.getInstance(context).getSavedLang()))
                    restartDialog(sLang(lang));
            }
        });
    }

    private void restartDialog(final String lang) {
        dialogBuilder.createAlertDialog(context, context.getResources().getString(R.string.restart_hint), new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                Common.Instance().setLang(context, lang);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restartTheApp();
                    }
                }, 400);
            }

            @Override
            public void onNegativeCLick() {

            }
        });
    }

    private void restartTheApp() {
        AlarmManager alm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SplashScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        alm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT));

        System.exit(0);
    }

    private String sLang(String lang) {
        switch (lang)
        {
            case "العربية":
                lang = "ar";
                break;
            case "English":
                lang = "en";
                break;
            default:
                break;
        }

        return lang;
    }

    @Override
    public int getItemCount() {
        return langList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView langLabel;
        Button btnDownloadLang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            langLabel = itemView.findViewById(R.id.lang_label);
            btnDownloadLang = itemView.findViewById(R.id.btn_download_lang);
        }
    }
}
