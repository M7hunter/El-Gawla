package it_geeks.info.elgawla.views.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it_geeks.info.elgawla.Adapters.WebViewAdapter;
import it_geeks.info.elgawla.repository.Models.WebPage;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.NotificationBuilder;
import it_geeks.info.elgawla.views.intro.SplashScreenActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.intro.IntroActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MenuFragment extends Fragment {

    private Context context;
    private RecyclerView webViewsRecycler;
    private ImageView imgNotification;

    private GoogleApiClient mGoogleApiClient;
    private DialogBuilder dialogBuilder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        handleEvents(view);

        initWebViewRecycler();
    }

    private void initViews(View view) {
        ImageView imCountryIcon = view.findViewById(R.id.menu_country_icon);
        webViewsRecycler = view.findViewById(R.id.web_views_recycler);

        //Notification icon
        imgNotification = view.findViewById(R.id.iv_notification_bell);
        View bellIndicator = view.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(context, bellIndicator);

        ImageLoader.getInstance().load(SharedPrefManager.getInstance(context).getCountry().getImage(), imCountryIcon);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createAlertDialog(getContext(), new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                SharedPrefManager.getInstance(getActivity()).clearUser();
                SharedPrefManager.getInstance(getActivity()).clearProvider();
                LoginManager.getInstance().logOut();
                if (mGoogleApiClient.isConnected())
                {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }

                disableNotification();
                startActivity(new Intent(getActivity(), SignInActivity.class));
                ((Activity) context).finish();
            }

            @Override
            public void onNegativeCLick() {

            }
        });
        dialogBuilder.setAlertText(getString(R.string.sign_out_hint));
    }

    private void handleEvents(View view) {
        // open settings page
        view.findViewById(R.id.menu_option_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SettingsActivity.class));
            }
        });


        // open vote page
        view.findViewById(R.id.vote_option_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VoteActivity.class));
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
                dialogBuilder.displayAlertDialog();
            }
        });

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void disableNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + SharedPrefManager.getInstance(context).getSubscribedSalonId());
        SharedPrefManager.getInstance(context).clearSubscribedSalonId();
    }

    private void initWebViewRecycler() {
        try
        {
            List<WebPage> pages = ((SplashScreenActivity) SplashScreenActivity.splashInstance).webPageList;
            if (pages != null && pages.size() > 0)
            {
                webViewsRecycler.setHasFixedSize(true);
                webViewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                webViewsRecycler.setAdapter(new WebViewAdapter(getContext(), pages));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
