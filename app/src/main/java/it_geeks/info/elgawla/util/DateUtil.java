package it_geeks.info.elgawla.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static long getCurrentTimeAsMillis() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeAsString() {
        return getDateAsStringFromMillis(System.currentTimeMillis());
    }

    public static long getDateAsMillisFromString(String dateString) {
        String myDate = dateString + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = null;
        try
        {
            date = sdf.parse(myDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date.getTime();
    }

    public static String getDateAsStringFromMillis(long dateInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(dateInMillis));
    }
}
