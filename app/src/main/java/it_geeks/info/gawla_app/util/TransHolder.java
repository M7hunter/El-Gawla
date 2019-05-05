package it_geeks.info.gawla_app.util;

import android.content.Context;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Storage.TransDao;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class TransHolder {

    private TransDao transDao;

    private String lang;

    // ------> keys <------ //
    // sign in activity keys
    public String sign_in;
    public String via_google_plus;
    public String via_facebook;
    public String email;
    public String password;
    public String forget_pass;
    public String create_account;

    // sign up activity keys
    public String sign_up;
    public String full_name;
    public String already_have_account;

    // forget pass activity keys
    public String forget_pass_hint;
    public String send;

    // main activity keys
    public String hales;
    public String my_rounds;
    public String cards;
    public String account;
    public String menu;

    // main fragment keys
    public String see_all;
    public String recent_salons;
    public String salons_winners;
    public String salons_empty_hint;

    // my rounds fragment keys
    public String joined_salons;
    public String rounds_empty_hint;

    // cards fragment keys
    public String cards_store;
    public String cards_empty_hint;

    // account fragment keys
    public String account_details;
    public String buying_processes;
    public String privacy_details;

    // menu fragment keys
    public String app_settings;
    public String more_about_gawla;
    public String privacy_policy;
    public String terms_and_conditions;
    public String call_us;
    public String how_gawla_works;
    public String sign_out;

    public TransHolder(Context context) {
        transDao = GawlaDataBse.getInstance(context).transDao();
        lang = getLang(context);
    }

    private String getLang(Context context) {
        return SharedPrefManager.getInstance(context).getSavedLang();
    }

    public void getSignInActivityTranses(Context context) {
        sign_in = transDao.getTransByKeyAndLang("sign_in", lang);
        if (sign_in == null || sign_in.isEmpty()) {
            sign_in = context.getResources().getString(R.string.sign_in);
        }

        via_google_plus = transDao.getTransByKeyAndLang("via_google_plus", lang);
        if (via_google_plus == null || via_google_plus.isEmpty()) {
            via_google_plus = context.getResources().getString(R.string.google_plus);
        }

        via_facebook = transDao.getTransByKeyAndLang("via_facebook", lang);
        if (via_facebook == null || via_facebook.isEmpty()) {
            via_facebook = context.getResources().getString(R.string.facebook);
        }

        email = transDao.getTransByKeyAndLang("email", lang);
        if (email == null || email.isEmpty()) {
            email = context.getResources().getString(R.string.email);
        }

        password = transDao.getTransByKeyAndLang("password", lang);
        if (password == null || password.isEmpty()) {
            password = context.getResources().getString(R.string.password);
        }

        forget_pass = transDao.getTransByKeyAndLang("forget_pass", lang);
        if (forget_pass == null || forget_pass.isEmpty()) {
            forget_pass = context.getResources().getString(R.string.forgetPassword);
        }

        create_account = transDao.getTransByKeyAndLang("create_account", lang);
        if (create_account == null || create_account.isEmpty()) {
            create_account = context.getResources().getString(R.string.create_account);
        }
    }

    public void getSignUpActivityTranses(Context context) {
        sign_up = transDao.getTransByKeyAndLang("sign_up", lang);
        if (sign_up == null || sign_up.isEmpty()) {
            sign_up = context.getResources().getString(R.string.sign_up);
        }

        via_google_plus = transDao.getTransByKeyAndLang("via_google_plus", lang);
        if (via_google_plus == null || via_google_plus.isEmpty()) {
            via_google_plus = context.getResources().getString(R.string.google_plus);
        }

        via_facebook = transDao.getTransByKeyAndLang("via_facebook", lang);
        if (via_facebook == null || via_facebook.isEmpty()) {
            via_facebook = context.getResources().getString(R.string.facebook);
        }

        full_name = transDao.getTransByKeyAndLang("full_name", lang);
        if (full_name == null || full_name.isEmpty()) {
            full_name = context.getResources().getString(R.string.full_name);
        }

        email = transDao.getTransByKeyAndLang("email", lang);
        if (email == null || email.isEmpty()) {
            email = context.getResources().getString(R.string.email);
        }

        password = transDao.getTransByKeyAndLang("password", lang);
        if (password == null || password.isEmpty()) {
            password = context.getResources().getString(R.string.password);
        }

        already_have_account = transDao.getTransByKeyAndLang("already_have_account", lang);
        if (already_have_account == null || already_have_account.isEmpty()) {
            already_have_account = context.getResources().getString(R.string.already_have_account);
        }
    }

    public void getForgetPassActivityTranses(Context context) {
        forget_pass = transDao.getTransByKeyAndLang("forget_pass", lang);
        if (forget_pass == null || forget_pass.isEmpty()) {
            forget_pass = context.getResources().getString(R.string.forgetPassword);
        }

        forget_pass_hint = transDao.getTransByKeyAndLang("forget_pass_hint", lang);
        if (forget_pass_hint == null || forget_pass_hint.isEmpty()) {
            forget_pass_hint = context.getResources().getString(R.string.wile_send_password);
        }

        email = transDao.getTransByKeyAndLang("email", lang);
        if (email == null || email.isEmpty()) {
            email = context.getResources().getString(R.string.email);
        }

        send = transDao.getTransByKeyAndLang("send", lang);
        if (send == null || send.isEmpty()) {
            send = context.getResources().getString(R.string.send);
        }
    }

    public void getMainActivityTranses(Context context) {
        hales = transDao.getTransByKeyAndLang("hales", lang);
        if (hales == null || hales.isEmpty()) {
            hales = context.getResources().getString(R.string.the_salons);
        }

        my_rounds = transDao.getTransByKeyAndLang("my_rounds", lang);
        if (my_rounds == null || my_rounds.isEmpty()) {
            my_rounds = context.getResources().getString(R.string.my_rounds);
        }

        cards = transDao.getTransByKeyAndLang("cards", lang);
        if (cards == null || cards.isEmpty()) {
            cards = context.getResources().getString(R.string.cards);
        }

        account = transDao.getTransByKeyAndLang("account", lang);
        if (account == null || account.isEmpty()) {
            account = context.getResources().getString(R.string.account);
        }

        menu = transDao.getTransByKeyAndLang("menu", lang);
        if (menu == null || menu.isEmpty()) {
            menu = context.getResources().getString(R.string.menu);
        }
    }

    public void getMainFragmentTranses(Context context) {
        see_all = transDao.getTransByKeyAndLang("see_all", lang);
        if (see_all == null || see_all.isEmpty()) {
            see_all = context.getResources().getString(R.string.see_all);
        }

        recent_salons = transDao.getTransByKeyAndLang("recent_salons", lang);
        if (recent_salons == null || recent_salons.isEmpty()) {
            recent_salons = context.getResources().getString(R.string.recent_salons);
        }

        salons_empty_hint = transDao.getTransByKeyAndLang("salons_empty_hint", lang);
        if (salons_empty_hint == null || salons_empty_hint.isEmpty()) {
            salons_empty_hint = context.getResources().getString(R.string.no_salons);
        }

        salons_winners = transDao.getTransByKeyAndLang("salons_winners", lang);
        if (salons_winners == null || salons_winners.isEmpty()) {
            salons_winners = context.getResources().getString(R.string.salons_winners);
        }
    }

    public void getMyRoundsFragmentTranses(Context context) {
        joined_salons = transDao.getTransByKeyAndLang("joined_salons", lang);
        if (joined_salons == null || joined_salons.isEmpty()) {
            joined_salons = context.getResources().getString(R.string.joined_salons);
        }

        rounds_empty_hint = transDao.getTransByKeyAndLang("rounds_empty_hint", lang);
        if (rounds_empty_hint == null || rounds_empty_hint.isEmpty()) {
            rounds_empty_hint = context.getResources().getString(R.string.rounds_empty_hint);
        }
    }

    public void getCardStoreFragmentTranses(Context context) {
        cards_store = transDao.getTransByKeyAndLang("cards_store", lang);
        if (cards_store == null || cards_store.isEmpty()) {
            cards_store = context.getResources().getString(R.string.cards_store);
        }

        cards_empty_hint = transDao.getTransByKeyAndLang("cards_empty_hint", lang);
        if (cards_empty_hint == null || cards_empty_hint.isEmpty()) {
            cards_empty_hint = context.getResources().getString(R.string.cards_empty_hint);
        }
    }

    public void getAccountFragmentTranses(Context context) {
        account_details = transDao.getTransByKeyAndLang("account_details", lang);
        if (account_details == null || account_details.isEmpty()) {
            account_details = context.getResources().getString(R.string.account_details);
        }

        buying_processes = transDao.getTransByKeyAndLang("buying_processes", lang);
        if (buying_processes == null || buying_processes.isEmpty()) {
            buying_processes = context.getResources().getString(R.string.buying_processes);
        }

        privacy_details = transDao.getTransByKeyAndLang("privacy_details", lang);
        if (privacy_details == null || privacy_details.isEmpty()) {
            privacy_details = context.getResources().getString(R.string.privacy_details);
        }
    }

    public void getMenuFragmentTranses(Context context) {
        app_settings = transDao.getTransByKeyAndLang("app_settings", lang);
        if (app_settings == null || app_settings.isEmpty()) {
            app_settings = context.getResources().getString(R.string.app_settings);
        }

        more_about_gawla = transDao.getTransByKeyAndLang("more_about_gawla", lang);
        if (more_about_gawla == null || more_about_gawla.isEmpty()) {
            more_about_gawla = context.getResources().getString(R.string.more_about_gawla);
        }

        privacy_policy = transDao.getTransByKeyAndLang("privacy_policy", lang);
        if (privacy_policy == null || privacy_policy.isEmpty()) {
            privacy_policy = context.getResources().getString(R.string.privacy_policy);
        }

        terms_and_conditions = transDao.getTransByKeyAndLang("terms_and_conditions", lang);
        if (terms_and_conditions == null || terms_and_conditions.isEmpty()) {
            terms_and_conditions = context.getResources().getString(R.string.terms_conditions);
        }

        call_us = transDao.getTransByKeyAndLang("call_us", lang);
        if (call_us == null || call_us.isEmpty()) {
            call_us = context.getResources().getString(R.string.call_us);
        }

        how_gawla_works = transDao.getTransByKeyAndLang("how_gawla_works", lang);
        if (how_gawla_works == null || how_gawla_works.isEmpty()) {
            how_gawla_works = context.getResources().getString(R.string.how_gawla_works);
        }

        sign_out = transDao.getTransByKeyAndLang("sign_out", lang);
        if (sign_out == null || sign_out.isEmpty()) {
            sign_out = context.getResources().getString(R.string.sign_out);
        }
    }
}
