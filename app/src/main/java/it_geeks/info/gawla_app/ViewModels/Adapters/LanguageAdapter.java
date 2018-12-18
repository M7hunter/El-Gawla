package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.MainActivity;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private Context context;
    private List<String> langsList;

    public LanguageAdapter(Context context, List<String> langsList) {
        this.context = context;
        this.langsList = langsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String lang = langsList.get(i);

        // set start up ui
        viewHolder.langLabel.setText(lang);

        String s = "";
        if (sLang(lang, s).equals(SharedPrefManager.getInstance(context).getSavedLang())) {
            viewHolder.langLabel.setTextColor(context.getResources().getColor(R.color.greenBlue));
        }

        // handle events
        viewHolder.langLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = SharedPrefManager.getInstance(context).getSavedLang();
                Common.Instance(context).setLang(sLang(lang, s));
                context.startActivity(new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private String sLang(String lang, String s) {
        switch (lang) {
            case "العربية":
                s = "ar";
                break;
            case "English":
                s = "en";
                break;
            default:
                break;
        }

        return s;
    }

    @Override
    public int getItemCount() {
        return langsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView langLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            langLabel = itemView.findViewById(R.id.lang_label);
        }
    }
}
