package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.PicassoClint;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.R;

public class AccountFragment extends Fragment {

    String name,image;
    TextView userName;
    CircleImageView userImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        getData();

        initViews(view);

        return view;
    }

    private void getData() {
       name = SharedPrefManager.getInstance(getContext()).getUser().getName();
       name = Common.Instance(getContext()).removeQuotes(name);
       image = SharedPrefManager.getInstance(getContext()).getUser().getImage();

    }


    private void initViews(View v) {
        userName = v.findViewById(R.id.user_name);
        userImage = v.findViewById(R.id.user_image);

        PicassoClint.downloadImage(getActivity(),image,userImage);
        userName.setText(name);

    }


}
