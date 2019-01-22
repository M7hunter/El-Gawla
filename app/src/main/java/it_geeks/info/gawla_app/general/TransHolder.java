package it_geeks.info.gawla_app.general;

import android.content.Context;

import it_geeks.info.gawla_app.Repositry.Storage.TransDao;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;

public class TransHolder {

    private TransDao transDao;

    public String see_all = "See all";
    public String recent_salons = "Recent Salons";
    public String winners_of_hales_news = "Winners Of Hales News";
    public String sign_in = "Sign In";

    public TransHolder(Context context) {
        transDao = GawlaDataBse.getGawlaDatabase(context).transDao();
        setTranses(getLang(context));
    }

    private String getLang(Context context) {
        return SharedPrefManager.getInstance(context).getSavedLang();
    }

    private void setTranses(String lang) {
        see_all = transDao.getTransByKeyAndLang("see_all", lang);
        recent_salons = transDao.getTransByKeyAndLang("recent_salons", lang);
        winners_of_hales_news = transDao.getTransByKeyAndLang("winners_of_hales_news", lang);
        sign_in = transDao.getTransByKeyAndLang("sign_in", lang);
    }
}
