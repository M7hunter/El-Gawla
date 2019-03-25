package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Adapters.WebViewAdapter;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.WebPage;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.SalonActivity;
import it_geeks.info.gawla_app.views.menuOptions.CallUsActivity;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.menuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.views.menuOptions.PrivacyPolicyActivity;
import it_geeks.info.gawla_app.views.menuOptions.SettingsActivity;
import it_geeks.info.gawla_app.views.menuOptions.TermsAndConditionsActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.splashActivities.IntroActivity;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.request.RequestActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MenuFragment extends Fragment implements View.OnTouchListener {

    private GoogleApiClient mGoogleApiClient;

    private TextView tvAppSettings, tvMoreAboutGawla, tvPrivacyPolicy, tvTermsAndCo, tvCallUs, tvHowGawlaWorks, tvSignOut; // <- trans
    private RecyclerView webViewsRecycler;
    ImageView imgNotification;
    private List<WebPage> webPageList = new ArrayList<>();

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;

    private int screenHeight, screenWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();

        initViews(view);

        setupTrans();

        screenDimensions();

        initCustomerServiceIcon(view);

        handleEvents(view);

        getWebPagesFromServer();

        return view;
    }

    private void initViews(View view) {

        //Customers Service
        Zendesk.INSTANCE.init(getApplicationContext(), "https://itgeeks.zendesk.com",
                "6d1749c16b1fa13aaf7a96a39614131f8eba1e5d27ed37bb",
                "mobile_sdk_client_e65a598574b57edaf2e8");
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
        Support.INSTANCE.init(Zendesk.INSTANCE);

        ImageView imCountryIcon = view.findViewById(R.id.menu_country_icon);
        webViewsRecycler = view.findViewById(R.id.web_views_recycler);

        //Notification icon
        imgNotification = view.findViewById(R.id.Notification);

        tvAppSettings = view.findViewById(R.id.tv_app_settings);
        tvMoreAboutGawla = view.findViewById(R.id.tv_more_about_gawla);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        tvTermsAndCo = view.findViewById(R.id.tv_terms_and_conditions);
        tvCallUs = view.findViewById(R.id.tv_call_us);
        tvHowGawlaWorks = view.findViewById(R.id.tv_how_gawla_works);
        tvSignOut = view.findViewById(R.id.tv_sign_out);

        Picasso.with(getContext()).load(SharedPrefManager.getInstance(getContext()).getCountry().getImage()).fit().into(imCountryIcon);
    }

    private void initCustomerServiceIcon(View view) {
        RelativeLayout customerServiceIconContainer = view.findViewById(R.id.customers_service_btn_container);
        customerServiceIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return true;
            }
        });
    }


    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getMenuFragmentTranses(getContext());

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
                        SharedPrefManager.getInstance(getActivity()).clearUser();
                        SharedPrefManager.getInstance(getActivity()).clearProvider();
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                        }

                        disableNotification();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        MainActivity.mainInstance.finish();
                    }
                });
                alertOut.show();
            }
        });

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), imgNotification);

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void disableNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + SharedPrefManager.getInstance(MainActivity.mainInstance).getSubscribedSalonId());
        SharedPrefManager.getInstance(MainActivity.mainInstance).clearSubscribedSalonId();
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        // just clicked
        if (gestureDetector.onTouchEvent(motionEvent)) {
            // Customers Service
            RequestActivity.builder().show(getContext());
        }

        // moved
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // move smoothly
                view.setX((int) (staringPoint.x + motionEvent.getX() - pointerPoint.x));
                view.setY((int) (staringPoint.y + motionEvent.getY() - pointerPoint.y));
                staringPoint.set(view.getX(), view.getY());

                break;
            case MotionEvent.ACTION_DOWN:
                // reinitialize points
                pointerPoint.set(motionEvent.getX(), motionEvent.getY());
                staringPoint.set(view.getX(), view.getY());

                break;
            case MotionEvent.ACTION_UP:
                // checks
                handleWithScreenBorders(view);
                view.performClick();

                break;
            default:
                break;
        }

        return true;
    }

    private void handleWithScreenBorders(View view) {
        // if x of the left border || in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2)) {
            view.animate().translationX(0).setDuration(250).start();
        }

        // if x of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2)) {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(250).start();
        }

        // if y of the up border
        if (view.getY() < 0) {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if y of the bottom border
        if (view.getY() >= (screenHeight - (view.getHeight()) * 2)) {
            view.animate().translationY(screenHeight - (view.getHeight() * 2)).setDuration(200).start();
        }
    }

    private void screenDimensions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }


}
