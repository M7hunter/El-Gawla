package it_geeks.info.gawla_app.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.SettingsActivity;

public class MenuFragment extends Fragment {

    private RelativeLayout optionSettings,Exit;
    SharedPrefManager sharedPreferences ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initViews(view);

        return view;
    }

    private void initViews(final View view) {

        optionSettings = view.findViewById(R.id.menu_settings);
        Exit = view.findViewById(R.id.menu_Exit);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = new SharedPrefManager(getActivity());
                sharedPreferences.logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        optionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

    }
}
