package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Country;

public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    public CountrySpinnerAdapter(Context context, ArrayList<Country> countries) {
        super(context, 0, countries);
    }

    @NonNull
    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country_spinner, parent, false);
        }

        TextView countryLabel = convertView.findViewById(R.id.item_spinner_text);

        Country country = getItem(position);

        if (country != null){
            countryLabel.setText(country.getCountry_title());
        }

        return convertView;
    }
}