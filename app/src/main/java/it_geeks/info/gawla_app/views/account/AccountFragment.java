package it_geeks.info.gawla_app.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class AccountFragment extends Fragment {

    private TextView userName;
    private CircleImageView userImage;
    private String name, image;

    ImageView ivNotificationBell;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);

        getData();

        handleEvents(view);

        return view;
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    private void initViews(View view) {
        userName = view.findViewById(R.id.user_name);
        userImage = view.findViewById(R.id.user_image);

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
                startActivity(new Intent(getContext(), AccountDetailsActivity.class));
            }
        });

        // privacy details page
        v.findViewById(R.id.cv_privacy_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyDetailsActivity.class));
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