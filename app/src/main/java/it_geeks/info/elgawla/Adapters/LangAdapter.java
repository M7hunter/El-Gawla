package it_geeks.info.elgawla.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Language;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface.OnItemClickListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

public class LangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Language> langList;
    private String selectedLang;
    private OnItemClickListener itemClickListener;

    public LangAdapter(Context context, List<Language> langList, OnItemClickListener itemClickListener) {
        this.langList = langList;
        this.itemClickListener = itemClickListener;

        selectedLang = SharedPrefManager.getInstance(context).getSavedLang();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder)
        {
            ((ViewHolder) holder).bind(langList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return langList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton rbLang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbLang = itemView.findViewById(R.id.rb_lang);
        }

        public void bind(final Language lang) {
            rbLang.setText(lang.getLabel());

            if (lang.getCode().equals(selectedLang))
            {
                rbLang.setChecked(true);
            }
            else
            {
                rbLang.setChecked(false);
            }

            rbLang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(rbLang, getAdapterPosition());
                }
            });
        }
    }
}