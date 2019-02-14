package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.WebViewAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.WebPage;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.menuOptions.CallUsActivity;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.menuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.views.menuOptions.PrivacyPolicyActivity;
import it_geeks.info.gawla_app.views.menuOptions.SettingsActivity;
import it_geeks.info.gawla_app.views.menuOptions.TermsAndConditionsActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.splashActivities.IntroActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MenuFragment extends Fragment {

    private GoogleApiClient mGoogleApiClient;

    private TextView tvMenuFragmentHint, tvAppSettings, tvMoreAboutGawla, tvPrivacyPolicy, tvTermsAndCo, tvCallUs, tvHowGawlaWorks, tvSignOut; // <- trans
    private RecyclerView webViewsRecycler;

    private List<WebPage> webPageList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();

        initViews(view);

        setupTrans();

        handleEvents(view);

        getWebPagesFromServer();

        return view;
    }

    private void initViews(View view) {
        ImageView imCountryIcon = view.findViewById(R.id.menu_country_icon);
        webViewsRecycler = view.findViewById(R.id.web_views_recycler);

        tvMenuFragmentHint = view.findViewById(R.id.tv_menu_fragment_hint);
        tvAppSettings = view.findViewById(R.id.tv_app_settings);
        tvMoreAboutGawla = view.findViewById(R.id.tv_more_about_gawla);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        tvTermsAndCo = view.findViewById(R.id.tv_terms_and_conditions);
        tvCallUs = view.findViewById(R.id.tv_call_us);
        tvHowGawlaWorks = view.findViewById(R.id.tv_how_gawla_works);
        tvSignOut = view.findViewById(R.id.tv_sign_out);

        Picasso.with(getContext()).load(SharedPrefManager.getInstance(getContext()).getCountry().getImage()).into(imCountryIcon);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getMenuFragmentTranses(getContext());

        tvMenuFragmentHint.setText(transHolder.menu_fragment_hint);
        tvAppSettings.setText(transHolder.app_settings);
        tvMoreAboutGawla.setText(transHolder.more_about_gawla);
        tvPrivacyPolicy.setText(transHolder.privacy_policy);
        tvTermsAndCo.setText(transHolder.terms_and_conditions);
        tvCallUs.setText(transHolder.call_us);
        tvHowGawlaWorks.setText(transHolder.how_gawla_works);
        tvSignOut.setText(transHolder.sign_out);
    }

    private void handleEvents(View view) {
        // open settings page
        view.findViewById(R.id.menu_option_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

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

        // terms & conditions page
        view.findViewById(R.id.menu_option_terms_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TermsAndConditionsActivity.class));
            }
        });

        // call us page
        view.findViewById(R.id.menu_option_call_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CallUsActivity.class));
            }
        });

        // intro page 'how gawla works'
        view.findViewById(R.id.menu_option_how_gawla_works).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), IntroActivity.class));
                IntroActivity.settingPage = true;
            }
        });

        // Logout the user
        view.findViewById(R.id.menu_option_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertOut = new AlertDialog.Builder(MainActivity.mainInstance);
                alertOut.setMessage(getString(R.string.sign_out_hint));
                alertOut.setNegativeButton(getString(R.string.cancel), null);
                alertOut.setPositiveButton(getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        SharedPrefManager.getInstance(getActivity()).clearUser();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        SharedPrefManager.getInstance(getActivity()).clearProvider();
                        getActivity().finish();
                    }
                });
                alertOut.show();
            }
        });

        // open Notification
        view.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void getWebPagesFromServer() {
        int user_id = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();

        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(), "getAllPages", new Request(user_id, api_token), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                webPageList = ParseResponses.parseWebPages(mainObject);

                initWebViewRecycler();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {

            }
        });
    }

    private void initWebViewRecycler() {
        webViewsRecycler.setHasFixedSize(true);
        webViewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        webViewsRecycler.setAdapter(new WebViewAdapter(getContext(), webPageList));
    }
}
