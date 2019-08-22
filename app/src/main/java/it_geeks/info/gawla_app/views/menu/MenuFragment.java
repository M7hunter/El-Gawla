package it_geeks.info.gawla_app.views.menu;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.view.MotionEvent;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it_geeks.info.gawla_app.Adapters.WebViewAdapter;
import it_geeks.info.gawla_app.repository.Models.WebPage;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.views.intro.SplashScreenActivity;
import it_geeks.info.gawla_app.views.login.LoginActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.main.NotificationActivity;
import it_geeks.info.gawla_app.views.intro.IntroActivity;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.request.RequestActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MenuFragment extends Fragment implements View.OnTouchListener {

    private Context context;
    private RecyclerView webViewsRecycler;
    private ImageView imgNotification;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;

    private int screenHeight, screenWidth;

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

        initHelp();

        screenDimensions();

        initCustomerServiceIcon(view);

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
        NotificationStatus.notificationStatus(context, bellIndicator);

        try
        {
            Picasso.with(context).load(SharedPrefManager.getInstance(context).getCountry().getImage()).fit().into(imCountryIcon);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        dialogBuilder = new DialogBuilder();
    }

    private void initHelp() {
        //Customers Service
        Zendesk.INSTANCE.init(getApplicationContext(), "https://itgeeks.zendesk.com",
                "6d1749c16b1fa13aaf7a96a39614131f8eba1e5d27ed37bb",
                "mobile_sdk_client_e65a598574b57edaf2e8");
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
        Support.INSTANCE.init(Zendesk.INSTANCE);
    }

    private void initCustomerServiceIcon(View view) {
        LinearLayout customerServiceIconContainer = view.findViewById(R.id.customers_service_btn_container);
        customerServiceIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return true;
            }
        });
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
                dialogBuilder.createAlertDialog(getContext(), getString(R.string.sign_out_hint), new ClickInterface.AlertButtonsClickListener() {
                    @Override
                    public void onPositiveClick() {
                        SharedPrefManager.getInstance(getActivity()).clearUser();
                        SharedPrefManager.getInstance(getActivity()).clearProvider();
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected())
                        {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                        }

                        disableNotification();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onNegativeCLick() {

                    }
                });
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
        List<WebPage> pages = ((SplashScreenActivity) SplashScreenActivity.splashInstance).webPageList;
        if (pages != null && pages.size() > 0)
        {
            webViewsRecycler.setHasFixedSize(true);
            webViewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            webViewsRecycler.setAdapter(new WebViewAdapter(getContext(), pages));
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // just clicked
        if (gestureDetector.onTouchEvent(motionEvent))
        {
            // Customers Service
            RequestActivity.builder().show(context);
        }

        // moved
        switch (motionEvent.getAction())
        {
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
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2))
        {
            view.animate().translationX(0).setDuration(250).start();
        }

        // if x of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2))
        {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(250).start();
        }

        // if y of the up border
        if (view.getY() < 0)
        {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if y of the bottom border
        if (view.getY() >= (screenHeight - (view.getHeight()) * 2))
        {
            view.animate().translationY(screenHeight - (view.getHeight() * 2)).setDuration(200).start();
        }
    }

    private void screenDimensions() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }
}
