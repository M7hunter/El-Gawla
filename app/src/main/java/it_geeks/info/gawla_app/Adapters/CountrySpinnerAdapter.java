package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.util.ImageLoader;

public class CountrySpinnerAdapter extends BaseAdapter {

    private List<Country> countryList;

    public CountrySpinnerAdapter(List<Country> countryList, Context context) {
        countryList.add(0, new Country(-178, context.getString(R.string.countries), "-000", "eg", "000", "000"));
        this.countryList = countryList;

    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countryList.get(position).getCountry_id();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country_spinner, parent, false);
        TextView tv = convertView.findViewById(R.id.tv_country_label);
        ImageView iv = convertView.findViewById(R.id.iv_country_flag);

        Country country = countryList.get(position);
        tv.setText(country.getCountry_title());
        if (position != 0)
        {
            ImageLoader.getInstance().load(country.getImage(), iv);
        }

        return convertView;
    }
}
