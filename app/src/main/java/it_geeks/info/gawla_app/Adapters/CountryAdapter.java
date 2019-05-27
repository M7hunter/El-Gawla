package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Country;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private Context context;
    private List<Country> countriesList;
    private ClickInterface.OnItemClickListener onItemClickListener;

    public CountryAdapter(Context context, List<Country> countriesList, ClickInterface.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.countriesList = countriesList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_country, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Country country = countriesList.get(i);

        viewHolder.name.setText(country.getCountry_title());
        Picasso.with(context).load(country.getImage()).placeholder(R.drawable.placeholder).into(viewHolder.flag);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView flag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.country_name);
            flag = itemView.findViewById(R.id.country_flag);
        }
    }
}
