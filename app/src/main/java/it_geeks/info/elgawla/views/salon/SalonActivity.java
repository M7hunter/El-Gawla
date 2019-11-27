package it_geeks.info.elgawla.views.salon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it_geeks.info.elgawla.Adapters.ActivityAdapter;
import it_geeks.info.elgawla.Adapters.TopTenAdapter;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.Floating.FloatingView;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.AudioPlayer;
import it_geeks.info.elgawla.repository.Models.Activity;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.TopTen;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.TourManager;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.util.receivers.ConnectionChangeReceiver;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.repository.Models.ProductSubImage;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.RoundRemainingTime;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.Adapters.SalonCardsAdapter;
import it_geeks.info.elgawla.Adapters.ProductSubImagesAdapter;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.util.salonUtils.CountDown.CountDownController;
import it_geeks.info.elgawla.util.salonUtils.ChatUtils;
import it_geeks.info.elgawla.util.salonUtils.SocketUtils;
import it_geeks.info.elgawla.views.store.PaymentMethodsActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_CODE_BUY_CARD;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_WITH_REALTIME;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_USER_CARDS_BY_SALON;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_TOP_TEN;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_WINNER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_USER_OFFER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_USER_SALON;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_ROUND_LEAVE;
import static it_geeks.info.elgawla.util.Constants.REQ_USE_GOLDEN_CARD;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class SalonActivity extends BaseActivity {

    // region fields
    private static final String TAG = "salon_connection_order";

    // region widgets
    private AlertDialog joinAlert;
    private ProgressBar joinProgress, joinConfirmationProgress, pbTopTen;
    public VideoView vpProductMainVideo;
    public ImageView ivProductMainViewer;
    private ImageView btnPlayPause, imgNotification, joinIcon, ivProductImage;
    public Button btnJoinRound, btnAddOffer;
    private Button btnJoinConfirmation, btnUseGoldenCard;
    private FloatingActionButton fbtnShare;
    private TextSwitcher tsRoundLatestActivity;
    public TextView tvChatEmptyHint, joinHeader, joinText, tvSalonMessage, tvProductDetailsTab, tvSalonActivityTab, tvChatTab, tvTopTenTab;
    private TextView tvCardsCount, tvActivityEmptyHint, tvTopTenEmptyHint, btnLeaveRound, tvWinnerName, tvWinnerLabel;
    private EditText etAddOffer;
    private TextInputLayout tilAddOffer;
    private View salonMainContainer, detailsSheetView, salonMainLayout;
    public View lastActivity, cardsBag, timeContainer;
    public LinearLayout addOfferLayout, chatContainer, topTenContainer, more;
    public RelativeLayout activityContainer;
    private RecyclerView activityRecycler, topTenRecycler, cardsRecycler;
    private SwipeRefreshLayout refreshLayout;
    // endregion

    // region objects
    private Salon salon;
    private Card goldenCard;
    public BottomSheetDialog mBottomSheetDialogCardsBag;
    private BottomSheetDialog mBottomSheetDialogProductDetails;
    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    public int userId;
    private int goldenCardCount = 0, stopPosition = 0, joinState; // 0 = watcher, 1 = want to join, 2 = joined
    private String userName, apiToken;

    private CountDownController countDownController;
    private RoundRemainingTime roundRemainingTime;
    private SocketUtils socketUtils;
    private ChatUtils chatUtils;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private ActivityAdapter activityAdapter;
    private MutableLiveData<String> winner = new MutableLiveData<>(), winnerImageUrl = new MutableLiveData<>();
    // endregion

    // region lists
    private List<Activity> activityList = new ArrayList<>();
    private List<Card> userCards = new ArrayList<>();
    // endregion
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        try
        {
            userName = SharedPrefManager.getInstance(SalonActivity.this).getUser().getName();
            registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            countDownController = new CountDownController(SalonActivity.this, findViewById(R.id.time_container));

            // entering sound
            AudioPlayer.getInstance().play(SalonActivity.this, R.raw.enter);

            initViews();

            if (getRoundData(savedInstanceState))
            {
                initBottomSheetCardsBag();
                bindProductMainViews();
                initBottomSheetProductDetails();
                initCardsBagIcon();

                requests();
            }

            handleEvents();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void requests() {
        getRemainingTimeFromServer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (countDownController != null)
            countDownController.setPause(false);

        if (roundRemainingTime != null)
            if (roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state() || roundRemainingTime.isRound_state() || roundRemainingTime.isRest_state())
            {
                getSocketUtils().connectSocket();
            }
            else
            {
                getSocketUtils().disconnectSocket();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getUserCardsForSalonFromServer();

        try
        {
            if (cardsRecycler != null && cardsRecycler.getAdapter() != null)
            {
                ((SalonCardsAdapter) cardsRecycler.getAdapter()).mBottomSheetDialogSingleCard.dismiss();
            }

            if (mBottomSheetDialogCardsBag.isShowing())
            {
                mBottomSheetDialogCardsBag.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (countDownController != null)
            countDownController.setPause(true);
    }

    @Override
    public void onBackPressed() {
        AudioPlayer.getInstance().play(SalonActivity.this, R.raw.exit);
        setResult(RESULT_OK, new Intent());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try
        {
            disconnectSalonSocket();
            unregisterReceiver(connectionChangeReceiver);
            countDownController.stopCountDown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void initViews() {
        salonMainLayout = findViewById(R.id.salon_main_layout);
        timeContainer = findViewById(R.id.time_container);
        activityRecycler = findViewById(R.id.salon_activity_recycler);
        topTenRecycler = findViewById(R.id.top_ten_recycler);

        tvWinnerName = findViewById(R.id.tv_salon_winner_name);
        tvWinnerLabel = findViewById(R.id.tv_salon_winner_label);

        activityRecycler.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        topTenRecycler.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));

        joinProgress = findViewById(R.id.join_progress);
        pbTopTen = findViewById(R.id.pb_top_ten);

        more = findViewById(R.id.cv_more);
        refreshLayout = findViewById(R.id.salon_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        topTenContainer = findViewById(R.id.top_ten_container);
        activityContainer = findViewById(R.id.activity_container);
        chatContainer = findViewById(R.id.chat_container);
        lastActivity = findViewById(R.id.cv_round_activity_container);

        btnAddOffer = findViewById(R.id.add_offer_btn);
        btnJoinRound = findViewById(R.id.btn_join_round);
        btnUseGoldenCard = findViewById(R.id.btn_use_golden_card);
        btnLeaveRound = findViewById(R.id.btn_leave_round);
        fbtnShare = findViewById(R.id.fbtn_share_salon);

        etAddOffer = findViewById(R.id.et_add_offer);
        tilAddOffer = findViewById(R.id.til_add_offer);

        tvCardsCount = findViewById(R.id.tv_cards_count);
        tvChatEmptyHint = findViewById(R.id.tv_chat_empty_hint);
        tvTopTenEmptyHint = findViewById(R.id.tv_top_ten_empty_hint);
        tvActivityEmptyHint = findViewById(R.id.tv_activity_empty_hint);
        tvTopTenTab = findViewById(R.id.tv_top_ten);
        tvProductDetailsTab = findViewById(R.id.tv_product_details);
        tvSalonActivityTab = findViewById(R.id.tv_salon_activity);
        tvChatTab = findViewById(R.id.tv_salon_chat);

        cardsBag = findViewById(R.id.cards_bag_btn_container);

        detailsSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);
        ivProductMainViewer = detailsSheetView.findViewById(R.id.product_details_main_image);

        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();

        imgNotification = findViewById(R.id.iv_notification_bell);
        View bellIndicator = findViewById(R.id.bell_indicator);

        NotificationBuilder.listenToNotificationStatus(this, bellIndicator);

        initActivitySwitcher();
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(salonMainLayout);
    }

    private boolean getRoundData(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();

            if (extras != null)
            { // get data from previous page
                salon = (Salon) extras.getSerializable(SALON);
            }
        }
        else
        { // get data from saved state
            salon = (Salon) savedInstanceState.getSerializable(SALON);
        }

        if (salon != null && salon.getSalon_cards() != null)
        {
            if (salon.getSalon_cards().isEmpty())
            {
                cardsBag.setVisibility(View.GONE);
            }
        }

        return salon != null;
    }

    private void handleEvents() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requests();
            }
        });

        // join
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySubscribeConfirmationLayout();
            }
        });

        // Leave Salon
        initUnsubscribeDialog();
        dialogBuilder.setAlertText(getString(R.string.leave_salon));
        btnLeaveRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.displayAlertDialog();
            }
        });

        // cancel confirmation
        getJoinAlert().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (joinState == 2 || roundRemainingTime.isUserJoin())
                {
                    onSubscriptionConfirmed();
                }
                else
                {
                    onSubscriptionCanceled();
                }
            }
        });

        // open product details sheet
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialogProductDetails != null)
                {
                    if (mBottomSheetDialogProductDetails.isShowing())
                    { // close sheet
                        mBottomSheetDialogProductDetails.dismiss();
                    }
                    else
                    {
                        mBottomSheetDialogProductDetails.show();
                        try
                        {
                            EventsManager.sendViewItemEvent(SalonActivity.this, "", salon.getProduct_id() + "", "", Double.parseDouble(salon.getProduct_commercial_price()));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }
                }
            }
        });

        // add offer
        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundRemainingTime.isUserJoin())
                {
                    sendOfferToServer();
                }
            }
        });

        // tabs
        tvTopTenTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTopTenTab();
            }
        });

        tvProductDetailsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDetailsTab();
            }
        });

        tvSalonActivityTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityTab();
            }
        });

        tvChatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChatUtils().selectChatTab();
            }
        });

        lastActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityTab();
            }
        });

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonActivity.this, NotificationActivity.class));
            }
        });

        fbtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLinkAndShareSalon();
            }
        });

        etAddOffer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if (Float.parseFloat(etAddOffer.getText().toString()) > Float.parseFloat(salon.getProduct_commercial_price()))
                    {
                        displayOfferError(getString(R.string.offer_less_than_price));
                        btnAddOffer.setEnabled(false);
                    }
                    else if (Float.parseFloat(etAddOffer.getText().toString()) < 1)
                    {
                        displayOfferError(getString(R.string.offer_more_than_one));
                        btnAddOffer.setEnabled(false);
                    }
                    else
                    {
                        btnAddOffer.setEnabled(true);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void createDynamicLinkAndShareSalon() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(buildUri())
                .setDomainUriPrefix("https://elgawlaapp.page.link/")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful())
                        {
                            // share
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, task.getResult().getShortLink().toString());
                            Intent intent = Intent.createChooser(shareIntent, getString(R.string.share_salon));
                            startActivity(intent);
                            EventsManager.sendShareEvent(SalonActivity.this, "salon", String.valueOf(salon.getSalon_id()));
                        }
                    }
                });
    }

    private Uri buildUri() {
        return new Uri.Builder()
                .scheme("https")
                .authority("elgawlaapp.page.link")
                .appendPath("salons")
                .appendQueryParameter("salon_id", String.valueOf(salon.getSalon_id()))
                .build();
    }

    private void sendOfferToServer() {
        sendOfferLayout();
        try
        {
            final String userOffer = etAddOffer.getText().toString();

            if (userOffer.isEmpty())
            {
                enableOfferLayout();
                etAddOffer.setText("");
                displayOfferError(getString(R.string.no_content));
                return;
            }
            else if (userOffer.equals(SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(salon.getSalon_id() + "" + userId)))
            {
                enableOfferLayout();
                return;
            }
            else
            {
                if (Float.parseFloat(userOffer) > Float.parseFloat(salon.getProduct_commercial_price()))
                {
                    displayOfferError(getString(R.string.offer_less_than_price));
                    enableOfferLayout();
                    etAddOffer.requestFocus();
                    return;
                }
                else if (Float.parseFloat(userOffer) < 1)
                {
                    displayOfferError(getString(R.string.offer_more_than_one));
                    enableOfferLayout();
                    etAddOffer.requestFocus();
                    return;
                }
            }

            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                    REQ_SET_USER_OFFER, new RequestModel<>(REQ_SET_USER_OFFER, SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                            , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                            , salon.getSalon_id()
                            , userOffer
                            , null, null, null), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            //Save user Offer
                            SharedPrefManager.getInstance(SalonActivity.this).saveUserOffer(salon.getSalon_id() + "" + userId, userOffer);
                            updateLatestActivity(mainObject.get("message").getAsString());

                            displayUserOffer();

                            try
                            {
                                JSONObject obj = new JSONObject();
                                obj.put("user", userName);
                                obj.put("salon_id", salon.getSalon_id());
                                obj.put("lang", SharedPrefManager.getInstance(SalonActivity.this).getSavedLang());
                                getSocketUtils().emitData("addOffer", obj);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }
                        }

                        @Override
                        public void handleAfterResponse() {
                            enableOfferLayout();
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            enableOfferLayout();
                            snackBuilder.setSnackText(errorMessage).showSnack();
                        }
                    });
        }
        catch (NumberFormatException e)
        {
            enableOfferLayout();
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void enableOfferLayout() {
        joinProgress.setVisibility(View.GONE);
        addOfferLayout.setVisibility(View.VISIBLE);
        btnAddOffer.setEnabled(true);
        etAddOffer.setEnabled(true);
    }

    private void sendOfferLayout() {
        joinProgress.setVisibility(View.VISIBLE);
        addOfferLayout.setVisibility(View.GONE);
        btnAddOffer.setEnabled(false);
        etAddOffer.setEnabled(false);
    }

    // region getters
    public Salon getSalon() {
        return salon;
    }

    public RoundRemainingTime getRoundRemainingTime() {
        if (roundRemainingTime == null)
        {
            getRemainingTimeFromServer();
        }
        return roundRemainingTime;
    }

    public ChatUtils getChatUtils() {
        if (chatUtils == null)
        {
            chatUtils = new ChatUtils(this, salonMainLayout);
        }
        return chatUtils;
    }

    public SocketUtils getSocketUtils() {
        if (socketUtils == null)
        {
            socketUtils = new SocketUtils(this);
        }
        return socketUtils;
    }

    public View getSnackBarContainer() {
        if (salonMainContainer == null)
        {
            salonMainContainer = findViewById(R.id.salon_main_layout);
        }
        return salonMainContainer;
    }

    private AlertDialog getJoinAlert() {
        if (joinAlert == null)
        {
            joinAlert = initJoinConfirmationDialog();
        }
        return joinAlert;
    }
    // endregion

    // region tabs
    private void selectTopTenTab() {
        getTopTen();
        topTenRecycler.setVisibility(View.VISIBLE);
        tvTopTenTab.setVisibility(View.VISIBLE);

        more.setVisibility(View.GONE);
        lastActivity.setVisibility(View.GONE);
        activityContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.VISIBLE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));

        // text color
        tvProductDetailsTab.setTextColor(Color.BLACK);
        tvSalonActivityTab.setTextColor(Color.BLACK);
        tvChatTab.setTextColor(Color.BLACK);
        tvTopTenTab.setTextColor(Color.WHITE);
    }

    private void initTopTenRecycler(List<TopTen> topTens) {
        if (topTens.size() > 0)
        {
            tvTopTenEmptyHint.setVisibility(View.GONE);
            topTenRecycler.setVisibility(View.VISIBLE);
            topTenRecycler.setHasFixedSize(true);
            topTenRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            topTenRecycler.setAdapter(new TopTenAdapter(topTens));
            Common.Instance().hideLoading(topTenRecycler, pbTopTen);
        }
        else
        {
            tvTopTenEmptyHint.setVisibility(View.VISIBLE);
            tvTopTenEmptyHint.setText(getString(R.string.top_ten_empty));
            topTenRecycler.setVisibility(View.GONE);
            pbTopTen.setVisibility(View.GONE);
        }
    }

    public void selectDetailsTab() {
        more.setVisibility(View.VISIBLE);
        lastActivity.setVisibility(View.VISIBLE);
        activityContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.GONE);

        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));

        // text color
        tvProductDetailsTab.setTextColor(Color.WHITE);
        tvSalonActivityTab.setTextColor(Color.BLACK);
        tvChatTab.setTextColor(Color.BLACK);
        tvTopTenTab.setTextColor(Color.BLACK);
    }

    public void updateLatestActivity(String notificationMsg) {
        try
        {
            tsRoundLatestActivity.setText(notificationMsg);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            Log.e("RoundLastActivity: ", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    private void selectActivityTab() {
        more.setVisibility(View.GONE);
        lastActivity.setVisibility(View.GONE);
        activityContainer.setVisibility(View.VISIBLE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.GONE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));

        // text color
        tvProductDetailsTab.setTextColor(Color.BLACK);
        tvSalonActivityTab.setTextColor(Color.WHITE);
        tvChatTab.setTextColor(Color.BLACK);
        tvTopTenTab.setTextColor(Color.BLACK);

        if (activityList.size() > 0)
        {
            tvActivityEmptyHint.setVisibility(View.GONE);
            activityRecycler.setVisibility(View.VISIBLE);
            activityRecycler.scrollToPosition(0);
        }
        else
        {
            tvActivityEmptyHint.setVisibility(View.VISIBLE);
            activityRecycler.setVisibility(View.GONE);
        }
    }

    public void initActivityRecycler() {
        if (activityList.size() > 0)
        {
            tvActivityEmptyHint.setVisibility(View.GONE);
            activityRecycler.setVisibility(View.VISIBLE);
            activityRecycler.setHasFixedSize(true);
            activityRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            activityAdapter = new ActivityAdapter(activityList);
            activityRecycler.setAdapter(activityAdapter);
        }
        else
        {
            tvActivityEmptyHint.setVisibility(View.VISIBLE);
            activityRecycler.setVisibility(View.GONE);
        }
    }

    public void updateActivityList(Activity activity) {
        activityList.add(0, activity);

        if (activityAdapter != null)
        {
            activityAdapter.notifyItemInserted(0);
        }
        else
        {
            initActivityRecycler();
        }

        activityRecycler.scrollToPosition(0);
    }
    // endregion

    // region salon time
    public void getRemainingTimeFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                REQ_GET_SALON_WITH_REALTIME, new RequestModel<>(REQ_GET_SALON_WITH_REALTIME, userId, apiToken, salon.getSalon_id()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        if (!mainObject.get("is_closed").getAsBoolean())
                        {
                            if (mainObject.get("isToday").getAsBoolean())
                            { // today ?
                                roundRemainingTime = ParseResponses.parseRoundRemainingTime(mainObject);
                                salon.setRound_id(roundRemainingTime.getLast_round_id());
                                initCountDown();
                            }
                            else
                            { // !today
                                tvSalonMessage.setText(salon.getMessage());
                            }
                        }
                        else
                        {
                            onSalonClosedState();
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        refreshLayout.setRefreshing(false);
                        TourManager.salonPageSequence(SalonActivity.this, findViewById(R.id.salon_message), findViewById(R.id.ll_salon_countdown));
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        refreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id.salon_main_layout), errorMessage, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getRemainingTimeFromServer();
                            }
                        }).show();
                    }
                });
    }

    private void initCountDown() {
        countDownController.setRoundRemainingTime(roundRemainingTime); // set salon remaining time
        if (roundRemainingTime.isUserJoin())
        { // update join state
            joinState = 2;
        }
        else
        {
            joinState = 0;
        }
    }

    public void checkOnTime() {
        if (roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state() || roundRemainingTime.isRound_state() || roundRemainingTime.isRest_state())
        {
            getSocketUtils().connectSocket();

            if (roundRemainingTime.isUserJoin())
            { // member?
                getChatUtils().enableChat();
            }
            else
            { // !member
                getChatUtils().disableChat();
            }
        }
        else
        {
            getChatUtils().disableChat();
        }

        if ((roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state()) && roundRemainingTime.isUserJoin())
        { // on join time & member
            btnLeaveRound.setVisibility(View.VISIBLE); // display leave salon btn
        }
        else
        { // !join time
            btnLeaveRound.setVisibility(View.GONE); // hide leave salon btn
        }

        if (roundRemainingTime.isPay_join_state() && !roundRemainingTime.isUserJoin())
        { // display golden card layout
            btnJoinRound.setVisibility(View.GONE);
            if (goldenCard != null)
            {
                displayGoldenLayout();
            }
            else
            {
                Log.d("Golden_card:", "id: null");
            }

        }
        else
        { // hide golden card layout
            hideGoldenLayout();
        }

        if (roundRemainingTime.isRound_state())
        {
            topTenRecycler.setVisibility(View.GONE);// hide top ten views
            tvTopTenTab.setVisibility(View.GONE);

            if (roundRemainingTime.isUserJoin())
            { // member ?
                addOfferLayout.setVisibility(View.VISIBLE); // display add offer layout
                displayUserOffer(); // get user last Offer
            }
        }
        else
        {
            addOfferLayout.setVisibility(View.GONE); // hide add offer layout
        }

        if (roundRemainingTime.isRest_state())
        {
            // clear user offer
            SharedPrefManager.getInstance(SalonActivity.this).clearUserOffer(salon.getSalon_id() + "" + userId);
            // display top ten
            selectTopTenTab();
            addOfferLayout.setVisibility(View.GONE);
        }

        if (roundRemainingTime.isClose_hall_state() || roundRemainingTime.getRound_status().equals("close"))
        {
            addOfferLayout.setVisibility(View.GONE);
            onSalonClosedState();
        }
    }

    private void onSalonClosedState() {
        selectTopTenTab();
        getWinner();
        getSocketUtils().disconnectSocket();
        getChatUtils().disableChat();
    }

    private void displayUserOffer() {
        tilAddOffer.setHint(String.valueOf(SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(salon.getSalon_id() + "" + userId)));
    }

    private void displayOfferError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void getTopTen() {
        Log.d(TAG, "getTopTen: doing");
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_GET_TOP_TEN, new RequestModel<>(REQ_GET_TOP_TEN, userId, apiToken, salon.getSalon_id(), salon.getRound_id()
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        try
                        {
                            initTopTenRecycler(ParseResponses.parseTopTen(mainObject));
                        }
                        catch (Exception e)
                        {
                            Log.e("getTopTen: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        Log.d(TAG, "getTopTen: done");
                        pbTopTen.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        pbTopTen.setVisibility(View.GONE);
                    }
                });
    }

    private void getWinner() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_GET_WINNER, new RequestModel<>(REQ_GET_WINNER, userId, apiToken, salon.getSalon_id(), salon.getRound_id()
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        try
                        {
                            String message = mainObject.get("message").getAsString();
                            snackBuilder.setSnackText(message).showSnack();

                            if (mainObject.get("is_winner").getAsBoolean())
                            {
                                winner.setValue(mainObject.get("user_name").getAsString());
                                winnerImageUrl.setValue(mainObject.get("user_image").getAsString());
                                String offer = mainObject.get("offer").getAsString();

                                if (userId == mainObject.get("user_id").getAsInt() && roundRemainingTime.isUserJoin())
                                { // winner ?
                                    EventsManager.sendSalonWinnerEvent(SalonActivity.this, Long.parseLong(offer), salon.getSalon_id(), userName);
                                    Intent i = new Intent(SalonActivity.this, WinnerActivity.class);
                                    i.putExtra("winner_name", winner.getValue());
                                    i.putExtra("offer", offer);
                                    startActivity(i);
                                }

                                Activity activity = new Activity(winner.getValue() + " " + message + offer, "");
                                updateLatestActivity(activity.getBody());
                                updateActivityList(activity);
                            }
                            else
                            {
                                winner.setValue(message);
                                winnerImageUrl.setValue("no_winner");
                            }
                        }
                        catch (NullPointerException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                    }
                });
    }
    // endregion

    // region subscribe to salon

    // region subscription layout
    private AlertDialog initJoinConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_salon_join_round_confirmation, null);

        joinIcon = dialogView.findViewById(R.id.join_alert_icon);
        joinHeader = dialogView.findViewById(R.id.join_alert_header);
        joinText = dialogView.findViewById(R.id.join_alert_text);
        joinConfirmationProgress = dialogView.findViewById(R.id.join_alert_progress);
        btnJoinConfirmation = dialogView.findViewById(R.id.btn_join_alert);

        btnJoinConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (joinState)
                {
                    case 0:
                        displaySubscribeConfirmationLayout();
                        break;
                    case 1:
                        subscribeUserToSalonOnServer();
                        break;
                    case 2:
                        onSubscriptionConfirmed();
                        updateLatestActivity(getString(R.string.you_are_joined));
                        break;
                    default:
                        break;
                }
            }
        });

        dialogBuilder.setView(dialogView);
        return dialogBuilder.create();
    }

    private void initSubscribeConfirmationViews() {
        joinIcon.setImageDrawable(getResources().getDrawable(R.drawable.q_mark_in_circle));
        joinHeader.setText(getString(R.string.Attention));
        joinHeader.setTextColor(getResources().getColor(R.color.midBlue));
        joinText.setText(getString(R.string.Attention_Details));
        btnJoinConfirmation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnJoinConfirmation.setText(getString(R.string.join_round));
    }

    private void displaySubscribeConfirmationLayout() {
        joinState = 1;
        countDownController.setUserJoin(false);
        ImageViewCompat.setImageTintList(joinIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));

        // display confirmation layout
        btnJoinRound.setVisibility(View.GONE);
        getJoinAlert().show();
    }

    private void congratsSubscribing() { // Congratulation Screen to Join Salon
        joinState = 2;
        countDownController.setUserJoin(true);
        btnLeaveRound.setVisibility(View.VISIBLE);
        subscribeUserToSalonNotification();

        joinIcon.setImageDrawable(getResources().getDrawable(R.drawable.joinedrounddone));
        ImageViewCompat.setImageTintList(joinIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greenBlue)));
        joinHeader.setText(getString(R.string.Congratulations_Attention));
        joinHeader.setTextColor(getResources().getColor(R.color.greenBlue));
        joinText.setText(getString(R.string.Congratulations_Attention_Details));
        btnJoinConfirmation.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        btnJoinConfirmation.setText(getString(R.string.start_play));
    }

    private void onSubscriptionConfirmed() {
        // hide confirmation layout
        btnJoinRound.setVisibility(View.GONE);
        addOfferLayout.setVisibility(View.GONE);
        btnUseGoldenCard.setVisibility(View.GONE);
        btnLeaveRound.setVisibility(View.VISIBLE);
        getJoinAlert().dismiss();
    }

    private void onSubscriptionCanceled() {
        joinState = 0;

        // hide confirmation layout
        btnJoinRound.setVisibility(View.VISIBLE);
        getJoinAlert().dismiss();
    }

    private void initUnsubscribeDialog() {
        dialogBuilder.createAlertDialog(SalonActivity.this, new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                unSubscribeUserFromSalonOnServer();
            }

            @Override
            public void onNegativeCLick() {

            }
        });
    }
    // endregion

    private void subscribeUserToSalonOnServer() {
        btnJoinConfirmation.setEnabled(false);
        joinConfirmationProgress.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                REQ_SET_USER_SALON, new RequestModel<>(REQ_SET_USER_SALON
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                        , salon.getSalon_id()
                        , String.valueOf(Common.Instance().getCurrentTimeInMillis())
                        , ""
                        , null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        congratsSubscribing();
                        getChatUtils().enableChat();
                        EventsManager.sendSubscribeToSalonEvent(SalonActivity.this, String.valueOf(salon.getSalon_id()));
                    }

                    @Override
                    public void handleAfterResponse() {
                        joinConfirmationProgress.setVisibility(View.GONE);
                        btnJoinConfirmation.setEnabled(true);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        joinConfirmationProgress.setVisibility(View.GONE);
                        btnJoinConfirmation.setEnabled(true);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void unSubscribeUserFromSalonOnServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                REQ_SET_ROUND_LEAVE, new RequestModel<>(REQ_SET_ROUND_LEAVE, userId, apiToken, salon.getSalon_id()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        AudioPlayer.getInstance().play(SalonActivity.this, R.raw.exit);
                        countDownController.stopCountDown();
                        btnLeaveRound.setVisibility(View.GONE);
                        unSubscribeUserFromSalonNotification();
                        getRemainingTimeFromServer();
                        initSubscribeConfirmationViews();
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Snackbar.make(findViewById(R.id.salon_main_layout), R.string.no_connection, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unSubscribeUserFromSalonOnServer();
                            }
                        }).show();
                    }
                });
    }

    private void subscribeUserToSalonNotification() {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("salon_" + salon.getSalon_id());
        }
        SharedPrefManager.getInstance(this).saveSubscribedSalonId(salon.getSalon_id());
    }

    private void unSubscribeUserFromSalonNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + salon.getSalon_id());
        SharedPrefManager.getInstance(this).clearSubscribedSalonId();
    }
    // endregion

    // region product details
    private void bindProductMainViews() {
        TextView tvProductName, tvProductPrice, tvSalonId;
        // init views
        tvSalonMessage = findViewById(R.id.salon_message);
        tvProductName = findViewById(R.id.salon_round_product_name);
        tvProductPrice = findViewById(R.id.salon_round_product_price);
        tvSalonId = findViewById(R.id.salon_number);
        ivProductImage = findViewById(R.id.salon_round_product_image);

        // set data
        tvProductName.setText(salon.getProduct_name());
        tvProductPrice.setText(salon.getProduct_commercial_price());
        tvSalonId.setText(String.valueOf(salon.getSalon_id()));


        Picasso.get()
                .load(salon.getProduct_image())
                .resize(800, 800)
                .onlyScaleDown()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .into(ivProductImage);
    }

    private void initBottomSheetProductDetails() {
        mBottomSheetDialogProductDetails = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        //init bottom sheet views
        initProductImagesRecycler(detailsSheetView);
        initProductDetails();

        //
        mBottomSheetDialogProductDetails.setContentView(detailsSheetView);
        Common.Instance().setBottomSheetHeight(detailsSheetView);
        mBottomSheetDialogProductDetails.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void initProductImagesRecycler(View parent) {
        if (salon != null)
            if (salon.getProduct_images() != null)
            {
                RecyclerView imagesRecycler = parent.findViewById(R.id.product_details_images_recycler);
                imagesRecycler.setHasFixedSize(true);
                imagesRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false));

                List<ProductSubImage> subImages = salon.getProduct_images();
                subImages.add(0, new ProductSubImage(salon.getProduct_id(), salon.getProduct_image()));
                imagesRecycler.setAdapter(new ProductSubImagesAdapter(this, subImages, salon.isClosed()));
            }
    }

    private void initProductDetails() {
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductDescription, tvCategoryLabel;
        // init views
        tvProductName = detailsSheetView.findViewById(R.id.product_details_name);
        tvProductCategory = detailsSheetView.findViewById(R.id.product_details_category);
        tvProductPrice = detailsSheetView.findViewById(R.id.product_details_price);
        tvProductDescription = detailsSheetView.findViewById(R.id.product_details_descriptions);
        tvCategoryLabel = detailsSheetView.findViewById(R.id.tv_category_label);

        vpProductMainVideo = detailsSheetView.findViewById(R.id.player);
        btnPlayPause = detailsSheetView.findViewById(R.id.btn_play_pause);

        // set data
        try
        {
            Picasso.get()
                    .load(salon.getProduct_image())
                    .resize(800, 800)
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(ivProductMainViewer);

            tvCategoryLabel.setText(getResources().getString(R.string.category) + ":");
            tvProductName.setText(salon.getProduct_name());
            tvProductCategory.setText(salon.getCategory_name());
            tvProductPrice.setText(salon.getProduct_commercial_price());
            tvProductDescription.setText(HtmlCompat.fromHtml(salon.getProduct_product_description(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void switchImageVideo(String url, Drawable drawable) {
        if (url.endsWith(".mp4") || url.endsWith(".3gp"))
        {
            ivProductMainViewer.setVisibility(View.INVISIBLE);
            vpProductMainVideo.setVisibility(View.VISIBLE);

            setupVideoPlayer(url);
        }
        else
        {
            vpProductMainVideo.setVisibility(View.INVISIBLE);
            ivProductMainViewer.setVisibility(View.VISIBLE);

            ivProductMainViewer.setImageDrawable(drawable);
        }
    }

    public void setupVideoPlayer(String url) {
        btnPlayPause.animate().alpha(1).start();

        Uri vidUri = Uri.parse(url);
        vpProductMainVideo.setVideoURI(vidUri);
        vpProductMainVideo.start();

        // hide pp btn
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (btnPlayPause.getAlpha() == 1)
                {
                    btnPlayPause.animate().alpha(0).setDuration(300).start();
                    btnPlayPause.setEnabled(false);
                }
            }
        };

        final Handler handler = new Handler();

        hidePP(handler, runnable);

        // display pp btn
        vpProductMainVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPlayPause.getAlpha() == 0)
                {
                    btnPlayPause.animate().alpha(1).setDuration(300).start();
                    btnPlayPause.setEnabled(true);
                    hidePP(handler, runnable);
                }
            }
        });

        // play || pause
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpProductMainVideo.isPlaying())
                {
                    if (vpProductMainVideo.canPause())
                    {
                        stopPosition = vpProductMainVideo.getCurrentPosition();
                        vpProductMainVideo.pause();
                        btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

                        handler.removeCallbacks(runnable);
                    }
                }
                else
                {
                    vpProductMainVideo.seekTo(stopPosition);
                    vpProductMainVideo.start();

                    btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    hidePP(handler, runnable);
                }
            }
        });
    }

    private void hidePP(Handler handler, Runnable runnable) {
        handler.postDelayed(runnable, 1500);
    }
    // endregion

    // region card

    // region golden card
    private void calculateGoldenCard() {
        for (int i = 0; i < salon.getSalon_cards().size(); i++)
        {
            if (salon.getSalon_cards().get(i).getCard_type().equals("gold"))
            {
                goldenCard = salon.getSalon_cards().get(i);

                goldenCardCount = 0;
                for (Card card : userCards)
                {
                    if (goldenCard != null)
                        if (card.getCard_type().equals(goldenCard.getCard_type()))
                        {
                            goldenCardCount = card.getCount();
                            goldenCard.setCount(card.getCount());
                            break;
                        }
                }

                initGoldenCardView();
                break;
            }
        }
    }

    private void initGoldenCardView() {
        if (goldenCard != null)
        {
            btnUseGoldenCard.setBackgroundColor(Color.parseColor(goldenCard.getCard_color()));
        }

        if (goldenCardCount > 0)
        {
            btnUseGoldenCard.setText(R.string.use_card_to_join);

        }
        else
        {
            btnUseGoldenCard.setText(R.string.buy_card_to_join);
        }

        btnUseGoldenCard.setEnabled(true);
        btnUseGoldenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goldenCardCount > 0)
                {
                    useGoldenCard();
                }
                else
                {
                    buyGoldenCard();
                }
            }
        });
    }

    public void displayGoldenLayout() {
        btnUseGoldenCard.setVisibility(View.VISIBLE);
    }

    public void hideGoldenLayout() {
        btnUseGoldenCard.setVisibility(View.GONE);
    }

    private void buyGoldenCard() {
        if (goldenCard != null)
        {
            Intent i = new Intent(this, PaymentMethodsActivity.class);
            i.putExtra("card_to_buy", goldenCard);
            i.putExtra("is_card", true);
            startActivityForResult(i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), REQ_CODE_BUY_CARD);
        }

    }

    public void useGoldenCard() {
        if (goldenCard != null)
        {
            dialogBuilder.displayLoadingDialog();
            hideGoldenLayout();
            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    REQ_USE_GOLDEN_CARD, new RequestModel<>(REQ_USE_GOLDEN_CARD, userId, apiToken, salon.getSalon_id(), goldenCard.getCard_id(), salon.getRound_id()
                            , null, null), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                            roundRemainingTime.setUserJoin(true);
                            goldenCard.setCount(goldenCardCount - 1);
                            getUserCardsForSalonFromServer();
                            getRemainingTimeFromServer();

                            congratsSubscribing();
                            getJoinAlert().show();
                        }

                        @Override
                        public void handleAfterResponse() {
                            dialogBuilder.hideLoadingDialog();
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            displayGoldenLayout();
                            dialogBuilder.hideLoadingDialog();
                            snackBuilder.setSnackText(errorMessage).showSnack();
                        }
                    });
        }
    }
    // endregion

    private void initCardsBagIcon() {
        new FloatingView(cardsBag, this).initViewListeners(new ClickInterface.SnackAction() {
            @Override
            public void onClick() {
                if (mBottomSheetDialogCardsBag.isShowing())
                {
                    mBottomSheetDialogCardsBag.dismiss();
                }
                else
                {
                    mBottomSheetDialogCardsBag.show();
                }
            }
        });
    }

    public void initBottomSheetCardsBag() {
        mBottomSheetDialogCardsBag = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_cards_bag, null);

        // init bottom sheet views
        if (salon != null && salon.getSalon_cards() != null)
        {
            cardsRecycler = sheetView.findViewById(R.id.salon_cards_bottom_recycler);
            cardsRecycler.setHasFixedSize(true);
            if (cardsRecycler.getLayoutManager() == null)
            {
                cardsRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));
            }

            getUserCardsForSalonFromServer(); // <-- refresh user store list
        }

        // close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_activate_cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogCardsBag.isShowing())
                {
                    mBottomSheetDialogCardsBag.dismiss();
                }
                else
                {
                    mBottomSheetDialogCardsBag.show();
                }
            }
        });

        //
        mBottomSheetDialogCardsBag.setContentView(sheetView);
        Common.Instance().setBottomSheetHeight(sheetView);
        mBottomSheetDialogCardsBag.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    public void getUserCardsForSalonFromServer() {
        int userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token();

        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                REQ_GET_USER_CARDS_BY_SALON, new RequestModel<>(REQ_GET_USER_CARDS_BY_SALON, userId, apiToken, salon.getSalon_id()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        userCards.clear();
                        userCards.addAll(ParseResponses.parseUserCardsBySalon(mainObject));

                        int allUserCardsCount = 0;
                        if (salon.getSalon_cards() != null)
                        {
                            for (Card userCard : userCards)
                            {
                                for (Card salonCard : salon.getSalon_cards())
                                {
                                    if (userCard.getCard_type().equals(salonCard.getCard_type()))
                                    {
                                        salonCard.setCount(userCard.getCount());
                                        break;
                                    }
                                }

                                allUserCardsCount = allUserCardsCount + userCard.getCount();
                            }
                        }

                        calculateGoldenCard();

                        // update store adapter
                        if (cardsRecycler.getAdapter() == null)
                        {
                            cardsRecycler.setAdapter(new SalonCardsAdapter(SalonActivity.this, salon.getSalon_cards(), salon.getSalon_id(), salon.getRound_id(), salonMainLayout));
                        }
                        else
                        {
                            cardsRecycler.getAdapter().notifyDataSetChanged();
                        }

                        if (userCards.size() > 0)
                        {
                            tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                        }
                        else
                        {
                            tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_red));
                        }

                        tvCardsCount.setText(String.valueOf(allUserCardsCount));
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        Log.d(TAG, "getUserCardsForSalonFromServer: done");
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Log.d(TAG, "getUserCardsForSalonFromServer: failed");
                    }
                });
    }
    // endregion

    // region extra
    private void disconnectSalonSocket() {
        if (salon != null)
        {
            try
            {
                JSONObject obj = new JSONObject();
                obj.put("user", SharedPrefManager.getInstance(SalonActivity.this).getUser().getName());
                obj.put("salon_id", salon.getSalon_id());
                obj.put("lang", SharedPrefManager.getInstance(SalonActivity.this).getSavedLang());

                getSocketUtils().emitData("leaveTyping", obj);
                getSocketUtils().emitData("leave", obj);
                getChatUtils().sendTypingState = true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        getSocketUtils().disconnectSocket();
    }

    private void initActivitySwitcher() {
        tsRoundLatestActivity = findViewById(R.id.ts_round_latest_activity);
        tsRoundLatestActivity.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(SalonActivity.this);
                tv.setMaxLines(2);
                tv.setTextSize(15);
                tv.setGravity(Gravity.TOP);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setText(getString(R.string.activity_empty_hint));
                tv.setTextColor(getResources().getColor(R.color.blueGrey));
                return tv;
            }
        });

        tsRoundLatestActivity.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        tsRoundLatestActivity.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
    }
    // endregion
}