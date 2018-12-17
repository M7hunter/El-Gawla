package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Views.MenuOptions.CallUsActivity;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.MenuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.Views.MenuOptions.PrivacyPolicyActivity;
import it_geeks.info.gawla_app.Views.MenuOptions.SettingsActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MenuFragment extends Fragment {

    private GoogleApiClient mGoogleApiClient;

    private ImageView imCountryIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initViews(view);

        return view;
    }

    private void initViews(final View view) {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        imCountryIcon = view.findViewById(R.id.menu_country_icon);

        Picasso.with(getContext()).load(SharedPrefManager.getInstance(getContext()).getCountry().getImage()).into(imCountryIcon);

        // more about gawla page
        view.findViewById(R.id.menu_option_more_about_gawla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MoreAboutGawlaActivity.class));
            }
        });

        // privacy policy page
        view.findViewById(R.id.menu_option_privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
            }
        });

        // call us page
        view.findViewById(R.id.menu_option_call_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CallUsActivity.class));
            }
        });

        // open settings page
        view.findViewById(R.id.menu_option_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        // Logout the user
        view.findViewById(R.id.menu_option_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getActivity()).clearUser();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                LoginManager.getInstance().logOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                SharedPrefManager.getInstance(getActivity()).clearProvider();
                getActivity().finish();
            }
        });
    }
}
