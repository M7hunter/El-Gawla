package it_geeks.info.gawla_app.Controllers.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private Context context;
    private List<String> langList;

    public LanguageAdapter(Context context, List<String> langList) {
        this.context = context;
        this.langList = langList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String lang = langList.get(i);

        // set start up ui
        viewHolder.langLabel.setText(lang);

        if (sLang(lang).equals(SharedPrefManager.getInstance(context).getSavedLang())) {
            viewHolder.langLabel.setTextColor(context.getResources().getColor(R.color.midBlue));
        }

        // events
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                context.startActivity(new Intent(context, MainActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                restartDialog(lang);
            }
        });

//        viewHolder.btnDownloadLang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadLangFromServer();

        // test
//                GawlaDataBse.getGawlaDatabase(context).transDao().insertTrans(new Trans("see_all", "test ar", "ar"));
//                GawlaDataBse.getGawlaDatabase(context).transDao().insertTrans(new Trans("see_all", "test en", "en"));
//                GawlaDataBse.getGawlaDatabase(context).transDao().insertTrans(new Trans("recent_salons", "recent salons ar", "ar"));
//                GawlaDataBse.getGawlaDatabase(context).transDao().insertTrans(new Trans("recent_salons", "recent salons en", "en"));
//            }
//        });
    }

    private void restartDialog(final String lang) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(context.getResources().getString(R.string.restart_hint))
                .setNegativeButton(context.getResources().getString(R.string.cancel), null)
                .setPositiveButton(context.getResources().getString(R.string.continue_), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.Instance(context).setLang(sLang(lang));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RestartTheApp();
                            }
                        }, 500);
                    }
                }).show();
    }

    private void RestartTheApp() {
        AlarmManager alm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        alm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT));

        System.exit(0);
    }

    private void downloadLangFromServer(String lang) {
        RetrofitClient.getInstance(context).executeConnectionToServer(context, "getLang",
                new Request(SharedPrefManager.getInstance(context).getUser().getUser_id(),
                        SharedPrefManager.getInstance(context).getUser().getApi_token(),
                        sLang(lang)), // <- new lang
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        GawlaDataBse.getGawlaDatabase(context).transDao().insertTranses(ParseResponses.parseLanguages(mainObject));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String sLang(String lang) {
        switch (lang) {
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
