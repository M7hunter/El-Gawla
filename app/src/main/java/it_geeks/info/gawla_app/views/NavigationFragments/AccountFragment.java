package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.views.accountOptions.AccountDetailsActivity;
import it_geeks.info.gawla_app.views.accountOptions.BuyingProcessesActivity;
import it_geeks.info.gawla_app.views.accountOptions.PrivacyDetailsActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class AccountFragment extends Fragment {

    TextView userName;
    public CircleImageView userImage;
    String name, image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        getData();

        initViews(view);

        setData();

        return view;
    }

    @Override
    public void onResume() {
        getData();
        setData();
        super.onResume();
    }

    private void getData() {  /// get data from sharedPreference
        name = SharedPrefManager.getInstance(getContext()).getUser().getName();
        image = SharedPrefManager.getInstance(getContext()).getUser().getImage();
    }

    private void initViews(View v) {  //  initialize Views
        userName = v.findViewById(R.id.user_name);
        userImage = v.findViewById(R.id.user_image);

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

        // open Notification
        v.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void setData() { // set data to views
        Picasso.with(getContext()).load(image).placeholder(R.drawable.placeholder).into(userImage);
        userName.setText(name);
    }

}