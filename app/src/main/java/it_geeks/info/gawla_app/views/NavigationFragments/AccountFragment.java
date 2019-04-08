package it_geeks.info.gawla_app.views.NavigationFragments;

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
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.accountActivities.AccountDetailsActivity;
import it_geeks.info.gawla_app.views.accountActivities.BuyingProcessesActivity;
import it_geeks.info.gawla_app.views.accountActivities.PrivacyDetailsActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class AccountFragment extends Fragment {

    private TextView userName;
    private CircleImageView userImage;
    private String name, image;

    ImageView ivNotificationBell;

    private TextView tvAccountDetails, tvBuyingProcesses, tvPrivacyDetails; // <- trans

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);

        getData();

        setupTrans();

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
        ivNotificationBell = view.findViewById(R.id.notification_bell);

        // translatable views
        tvAccountDetails = view.findViewById(R.id.tv_account_details);
        tvBuyingProcesses = view.findViewById(R.id.tv_buying_processes);
        tvPrivacyDetails = view.findViewById(R.id.tv_privacy_details);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getAccountFragmentTranses(getContext());

        tvAccountDetails.setText(transHolder.account_details);
        tvBuyingProcesses.setText(transHolder.buying_processes);
        tvPrivacyDetails.setText(transHolder.privacy_details);
    }

    private void handleEvents(View v) {
        //intent to account details
        v.findViewById(R.id.cv_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountDetailsActivity.class));
            }
        });

        //intent to Privacy details
        v.findViewById(R.id.cv_privacy_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyDetailsActivity.class));
            }
        });

        // open buying processes page
        v.findViewById(R.id.account_option_buying_processes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BuyingProcessesActivity.class));
            }
        });

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), ivNotificationBell);

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