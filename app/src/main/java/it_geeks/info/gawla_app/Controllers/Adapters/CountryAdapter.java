package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.General.OnItemClickListener;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Context context;
    private List<Country> countriesList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = 0;

    public CountryAdapter(Context context, List<Country> countriesList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.countriesList = countriesList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_country, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Country country = countriesList.get(i);

        // check if selected
        if (selectedPosition == i) { // selected ?
            if (SharedPrefManager.getInstance(context).getCountry().getCountry_id() == country.getCountry_id()) { // my country ?
                myCountry(viewHolder);
            } else {
                viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white_bordered_nice_blue));
            }
        } else { // !selected
            if (SharedPrefManager.getInstance(context).getCountry().getCountry_id() == country.getCountry_id()) { // my country ?
                myCountry(viewHolder);
            } else {
                notMyCountry(viewHolder);
            }
        }

        viewHolder.name.setText(country.getCountry_title());
        Picasso.with(context).load(country.getImage()).placeholder(R.drawable.placeholder).into(viewHolder.icon);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());

                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    private void myCountry(ViewHolder viewHolder) {
        viewHolder.name.setTextColor(Color.WHITE);
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_nice_blue));
    }

    private void notMyCountry(ViewHolder viewHolder) {
        viewHolder.name.setTextColor(context.getResources().getColor(R.color.battleshipGrey));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white));
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.country_name);
            icon = itemView.findViewById(R.id.country_icon);
        }
    }
}
