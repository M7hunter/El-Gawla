package it_geeks.info.gawla_app.util;

import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import it_geeks.info.gawla_app.R;

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
}
