package it_geeks.info.gawla_app.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.main.NotificationActivity;

public class AccountFragment extends Fragment {

    private TextView userName;
    private Button btnRenewMemberShip;
    private ImageView ivNotificationBell;
    private CircleImageView userImage;

    private String name, image;

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
    }

    private void initViews(View view) {
        userName = view.findViewById(R.id.user_name);
        userImage = view.findViewById(R.id.user_image);
        btnRenewMemberShip = view.findViewById(R.id.btn_renew_membership);

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
                startActivity(new Intent(getContext(), BuyingProcessesActivity.class));
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
        Picasso.with(getContext()).load(image).placeholder(R.drawable.placeholder).into(userImage);
        userName.setText(name);
    }
}