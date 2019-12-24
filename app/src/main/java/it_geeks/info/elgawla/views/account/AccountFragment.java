package it_geeks.info.elgawla.views.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.NotificationActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_CHECK_SUBSCRIPTION;

public class AccountFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private TextView userName;
    private TextSwitcher tsSubscription;
    private ImageView ivNotificationBell;
    private CircleImageView userImage;
    private View llExp, btnRenewMemberShip;

    private String name, image;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        handleEvents(view);

        SharedPrefManager.getInstance(context).getUserSubscription().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String subscription) {
                Log.d("subscription", "subscription: " + subscription);
                if (subscription.equals("Need Subscribe") || subscription.equals("0"))
                {
                    llExp.setVisibility(View.GONE);
                }
                else
                {
                    llExp.setVisibility(View.VISIBLE);
                    if (!((TextView) tsSubscription.getCurrentView()).getText().toString().equals(subscription))
                        tsSubscription.setText(subscription);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();

        checkSubscriptionOnServer();
    }

    private void initViews(View view) {
        refreshLayout = view.findViewById(R.id.fragment_account_main_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        userName = view.findViewById(R.id.user_name);
        userImage = view.findViewById(R.id.user_image);
        btnRenewMemberShip = view.findViewById(R.id.btn_renew_membership);
        llExp = view.findViewById(R.id.ll_exp);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkSubscriptionOnServer();
            }
        });


        // subscription TextSwitcher
        tsSubscription = view.findViewById(R.id.ts_subscription_exp);
        tsSubscription.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(context);
                tv.setTextSize(15);
                tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(getResources().getColor(R.color.darkSkyBlue));
                return tv;
            }
        });

        tsSubscription.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom));

        //Notification icon
        ivNotificationBell = view.findViewById(R.id.iv_notification_bell);
        View bellIndicator = view.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(context, bellIndicator);
    }

    private void handleEvents(View v) {
        // account details page
        v.findViewById(R.id.cv_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });

        // privacy details page
        v.findViewById(R.id.cv_privacy_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        // buying processes page
        v.findViewById(R.id.account_option_buying_processes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InvoicesActivity.class));
            }
        });

        // my cards page
        v.findViewById(R.id.cv_my_cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyCardsActivity.class));
            }
        });

        ivNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        btnRenewMemberShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MembershipActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });
    }

    private void getData() {  /// get data from sharedPreference
        name = SharedPrefManager.getInstance(getContext()).getUser().getName();
        image = SharedPrefManager.getInstance(getContext()).getUser().getImage();

        setData();
    }

    private void setData() { // set data to views
        ImageLoader.getInstance().load(image, userImage);
        userName.setText(name);
    }

    private void checkSubscriptionOnServer() {
        RetrofitClient.getInstance(context).fetchDataFromServer(context, REQ_CHECK_SUBSCRIPTION
                , new RequestModel<>(REQ_CHECK_SUBSCRIPTION, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        SharedPrefManager.getInstance(context).setUserSubscription(mainObject.get("subscribe_end").getAsString());
                    }

                    @Override
                    public void afterResponse() {
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        refreshLayout.setRefreshing(false);
                    }
                });
    }
}