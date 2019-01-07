package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.views.splashActivities.IntroActivity;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Context context;
    private List<Country> countriesList;

    public CountryAdapter(Context context, List<Country> countriesList) {
        this.context = context;
        this.countriesList = countriesList;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_country, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Country country = countriesList.get(i);

        viewHolder.label.setText(country.getCountry_title());
        Picasso.with(context).load(country.getImage()).placeholder(R.drawable.placeholder).into(viewHolder.icon);

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(context).setCountry(country);

                context.startActivity(new Intent(context, IntroActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        ImageView icon;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.country_child_label);
            icon = itemView.findViewById(R.id.country_child_icon);
            container = itemView.findViewById(R.id.country_child_container);
        }
    }
}
