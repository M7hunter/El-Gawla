package it_geeks.info.elgawla.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EventsManager {

    private static FirebaseAnalytics firebaseAnalytics;

    private static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        if (firebaseAnalytics == null)
        {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return firebaseAnalytics;
    }

    public static void sendSignUpEvent(Context context, String method) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    public static void sendSignInEvent(Context context, String method) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void sendShareEvent(Context context, String contentType, String itemId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    public static void sendSearchEvent(Context context, String searchTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    public static void sendSearchResultsEvent(Context context, String searchTerm) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }

    public static void sendSendMessageToCallUsEvent(Context context, String currency, double value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.GENERATE_LEAD, bundle);
    }

    public static void sendOpenMembershipEvent(Context context, String itemCategory) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    public static void sendChoosePaymentMethodEvent(Context context, String coupon, String currency, double value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
    }

    public static void sendOpenPaymentEvent(Context context) {
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, null);
    }

    public static void sendPaymentReviewEvent(Context context, String coupon, String currency, double shipping, double tax, String transactionId, double value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.SHIPPING, shipping);
        bundle.putDouble(FirebaseAnalytics.Param.TAX, tax);
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
    }

    public static void sendSubscribeToSalonEvent(Context context, String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.GROUP_ID, groupId);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.JOIN_GROUP, bundle);
    }

    public static void sendViewItemEvent(Context context, String currency, String itemId, String itemLocationId, double value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_LOCATION_ID, itemLocationId);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    public static void sendSalonLevelEvent(Context context, long level, String levelName) {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.LEVEL, level);
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, levelName);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.LEVEL_UP, bundle);
    }

    public static void sendSalonWinnerEvent(Context context,long score, long level, String levelName) {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.SCORE, score);
        bundle.putLong(FirebaseAnalytics.Param.LEVEL, level);
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, levelName);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
    }

    public static void sendNotificationInteractionEvent(Context context, String itemCategory, String itemId, String itemName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.PRESENT_OFFER, bundle);
    }

    static void sendTutorialBeginEvent(Context context) {
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);
    }

    static void sendTutorialCompleteEvent(Context context) {
        getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
    }

    public static void SendCustomEvent(Context context, String p1, String p2) {
        Bundle params = new Bundle();
        params.putString("HAWAS", p1);
        params.putString("WAHEED", p2);
        getFirebaseAnalytics(context).logEvent("open_store_WM", params);
    }
}
