package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import it_geeks.info.gawla_app.R;

 public class PicassoClint {

    static String imgs;



    public static void downloadImage(Context c, String url, ImageView img)
    {

        if(url != null && url.length()>0)
        {
            Picasso.with(c).load(url).placeholder(R.drawable.gawla_logo_blue).into(img);
        }
        else
        {
            Picasso.with(c).load(R.drawable.gawla_logo_blue).into(img);
        }
    }
}
