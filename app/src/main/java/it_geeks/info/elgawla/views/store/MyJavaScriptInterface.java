package it_geeks.info.elgawla.views.store;

import android.util.Log;
import android.webkit.JavascriptInterface;

class MyJavaScriptInterface {

    static String PaymentId, Result, Amount, Transaction, Track, Date;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postMessage(String value) {
        Log.d("MyJavaScriptInterface", "postMessage: " + value);
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postPaymentId(String value) {
        Log.d("MyJavaScriptInterface", "postPaymentId: " + value);
        PaymentId = value;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postResult(String value) {
        Log.d("MyJavaScriptInterface", "postResult: " + value);
        Result = value;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postAmount(String value) {
        Log.d("MyJavaScriptInterface", "postAmount: " + value);
        Amount = value;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postTransaction(String value) {
        Log.d("MyJavaScriptInterface", "postTransaction: " + value);
        Transaction = value;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postTrack(String value) {
        Log.d("MyJavaScriptInterface", "postTrack: " + value);
        Track = value;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void postDate(String value) {
        Log.d("MyJavaScriptInterface", "postDate: " + value);
        Date = value;
    }
}
