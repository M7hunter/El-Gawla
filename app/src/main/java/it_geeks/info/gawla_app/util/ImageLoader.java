package it_geeks.info.gawla_app.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class ImageLoader {

    private static ImageLoader imageLoader;

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    public void loadImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(imageView.getContext())
                    .load(imageUrl)
                    .resize(800, 800)
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    public void loadIcon(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(imageView.getContext())
                    .load(imageUrl)
//                    .resize(100, 100)
//                    .onlyScaleDown()
//                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void loadLandscapeImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(imageView.getContext())
                    .load(imageUrl)
                    .resize(900, 600)
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void loadFitImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(imageView.getContext())
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void loadUserImage(Context context, ImageView imageView) {
        // load User Image
        ImageLoader.getInstance().loadFitImage(SharedPrefManager.getInstance(context).getUser().getImage(), imageView);
    }
}
