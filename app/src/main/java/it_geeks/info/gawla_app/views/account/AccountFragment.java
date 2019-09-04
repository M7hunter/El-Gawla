package it_geeks.info.gawla_app.views.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.main.NotificationActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_CHECK_SUBSCRIPTION;

public class AccountFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private TextView userName, tvSubscriptionExp;
    private Button btnRenewMemberShip;
    private ImageView ivNotificationBell;
    private CircleImageView userImage;
    private View llExp, pbExp;

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
        tvSubscriptionExp = view.findViewById(R.id.tv_subscription_exp);
        userImage = view.findViewById(R.id.user_image);
        btnRenewMemberShip = view.findViewById(R.id.btn_renew_membership);
        pbExp = view.findViewById(R.id.pb_exp);
        llExp = view.findViewById(R.id.ll_exp);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkSubscriptionOnServer();
            }
        });


        //Notification icon
        ivNotificationBell = view.findViewById(R.id.iv_notification_bell);
        View bellIndicator = view.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), bellIndicator);
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

        // my victories page
        v.findViewById(R.id.cv_my_victories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SalonsArchiveActivity.class));
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
                startActivity(new Intent(getContext(), MembershipActivity.class));
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
        RetrofitClient.getInstance(context).executeConnectionToServer(context, REQ_CHECK_SUBSCRIPTION
                , new Request<>(REQ_CHECK_SUBSCRIPTION, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String subscription = mainObject.get("subscribe_end").getAsString();
                        if (subscription != null)
                            if (subscription.equals("Need Subscribe") || subscription.equals("0")) {
                                llExp.setVisibility(View.GONE);
                            } else {
                                llExp.setVisibility(View.VISIBLE);
                                tvSubscriptionExp.setText(subscription);
                            }
                    }

                    @Override
                    public void handleAfterResponse() {
                        pbExp.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        pbExp.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }
}