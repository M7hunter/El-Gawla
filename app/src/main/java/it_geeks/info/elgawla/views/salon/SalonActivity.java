package it_geeks.info.elgawla.views.salon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.ActivityAdapter;
import it_geeks.info.elgawla.Adapters.TopTenAdapter;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.AudioPlayer;
import it_geeks.info.elgawla.repository.Models.Activity;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.TopTen;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.util.receivers.ConnectionChangeReceiver;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.repository.Models.ProductSubImage;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.Models.Round;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.RoundRemainingTime;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.Adapters.SalonCardsAdapter;
import it_geeks.info.elgawla.Adapters.ProductSubImagesAdapter;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.util.salonUtils.CountDown.CountDownController;
import it_geeks.info.elgawla.util.salonUtils.ChatUtils;
import it_geeks.info.elgawla.util.salonUtils.SocketUtils;

import static it_geeks.info.elgawla.util.Constants.REQ_CODE_BUY_CARD;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_WITH_REALTIME;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_USER_CARDS_BY_SALON;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_TOP_TEN;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_WINNER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_USER_OFFER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_USER_SALON;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_ROUND_LEAVE;
import static it_geeks.info.elgawla.util.Constants.REQ_USE_GOLDEN_CARD;
import static it_geeks.info.elgawla.util.Constants.REQ_ADD_CARDS_TO_USER;

public class SalonActivity extends AppCompatActivity {

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
    private TextSwitcher tsRoundLatestActivity;
    public TextView tvChatEmptyHint, joinHeader, joinText, tvSalonMessage, tvProductDetailsTab, tvSalonActivityTab, tvChatTab, tvTopTenTab;
    private TextView tvCardsCount, tvActivityEmptyHint, tvTopTenEmptyHint, btnLeaveRound;
    private EditText etAddOffer;
    private TextInputLayout tilAddOffer;
    private View salonMainContainer, detailsSheetView, salonMainLayout;
    public View lastActivity;
    public LinearLayout addOfferLayout, activityContainer, chatContainer, topTenContainer, more;
    private RecyclerView activityRecycler, topTenRecycler, cardsRecycler;
    // endregion

    // region objects
    private Round round;
    private Card goldenCard;
    public ProductSubImage SubImage = new ProductSubImage();
    public BottomSheetDialog mBottomSheetDialogCardsBag;
    private BottomSheetDialog mBottomSheetDialogProductDetails;
    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    public int userId;
    private int goldenCardCount = 0, stopPosition = 0, screenHeight, screenWidth, joinState; // 0 = watcher, 1 = want to join, 2 = joined
    private String userName, apiToken;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;

    private CountDownController countDownController;
    private RoundRemainingTime roundRemainingTime;
    private SocketUtils socketUtils;
    private ChatUtils chatUtils;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private ActivityAdapter activityAdapter;
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

        userName = SharedPrefManager.getInstance(SalonActivity.this).getUser().getName();
        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        countDownController = new CountDownController(SalonActivity.this, findViewById(R.id.time_container));

        // entering sound
        AudioPlayer.getInstance().play(SalonActivity.this, R.raw.enter);

        initViews();

        if (getRoundData(savedInstanceState))
        {
            initBottomSheetCardsBag();
            getRemainingTimeFromServer();
            bindProductMainViews();
            initBottomSheetProductDetails();

            initJoinConfirmationDialog();

            getScreenDimensions();

            initCardsBagIcon();

            getChatUtils();

            getSocketUtils();
        }

        handleEvents();

//        initHelp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (countDownController != null)
            countDownController.setPause(false);

        if (roundRemainingTime != null)
            if (roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state() || roundRemainingTime.isFirst_round_state() || roundRemainingTime.isFirst_rest_state() || roundRemainingTime.isSecond_round_state())
            {
                socketUtils.connectSocket();
            } else
            {
                socketUtils.disconnectSocket();
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
        disconnectSalonSocket();
        unregisterReceiver(connectionChangeReceiver);

        try
        {
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
        activityRecycler = findViewById(R.id.salon_activity_recycler);
        topTenRecycler = findViewById(R.id.top_ten_recycler);

        joinProgress = findViewById(R.id.join_progress);
        pbTopTen = findViewById(R.id.pb_top_ten);

        more = findViewById(R.id.cv_more);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        topTenContainer = findViewById(R.id.top_ten_container);
        activityContainer = findViewById(R.id.activity_container);
        chatContainer = findViewById(R.id.chat_container);
        lastActivity = findViewById(R.id.cv_round_activity_container);

        btnAddOffer = findViewById(R.id.add_offer_btn);
        btnJoinRound = findViewById(R.id.btn_join_round);
        btnUseGoldenCard = findViewById(R.id.btn_use_golden_card);
        btnLeaveRound = findViewById(R.id.btn_leave_round);

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

        detailsSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);
        ivProductMainViewer = detailsSheetView.findViewById(R.id.product_details_main_image);

        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();

        imgNotification = findViewById(R.id.iv_notification_bell);
        View bellIndicator = findViewById(R.id.bell_indicator);

        // notification status LiveData
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
                round = (Round) extras.getSerializable("round");
            }
        } else
        { // get data from saved state
            round = (Round) savedInstanceState.getSerializable("round");
        }

        return round != null;
    }

    private void handleEvents() {
        // join
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySubscribeConfirmationLayout();
            }
        });

        // Leave Round
        initUnsubscribeDialog();
        dialogBuilder.setAlertText(getString(R.string.leave_salon));
        btnLeaveRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.displayAlertDialog();
            }
        });

        // cancel confirmation
        joinAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (joinState == 2 || roundRemainingTime.isUserJoin())
                {
                    onSubscriptionConfirmed();
                } else
                {
                    onSubscriptionCanceled();
                }
            }
        });

        // open product details sheet
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialogProductDetails != null && mBottomSheetDialogProductDetails.isShowing())
                { // close sheet
                    mBottomSheetDialogProductDetails.dismiss();
                } else
                {
                    mBottomSheetDialogProductDetails.show();
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
                chatUtils.selectChatTab();
            }
        });

        findViewById(R.id.round_latest_activity).setOnClickListener(new View.OnClickListener() {
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

        etAddOffer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if (Integer.parseInt(etAddOffer.getText().toString()) > Integer.parseInt(round.getProduct_commercial_price()))
                    {
                        tilAddOffer.setError(getString(R.string.offer_less_than_price));
                        btnAddOffer.setEnabled(false);
                    } else
                    {
                        btnAddOffer.setEnabled(true);
                        if (tilAddOffer.getError() != null)
                            tilAddOffer.setError(null);
                    }
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendOfferToServer() {
        etAddOffer.setEnabled(false);
        addOfferLayout.setVisibility(View.GONE);
        joinProgress.setVisibility(View.VISIBLE);
        try
        {
            final String userOffer = etAddOffer.getText().toString();

            if (userOffer.isEmpty() || userOffer.equals("0"))
            {
                joinProgress.setVisibility(View.GONE);
                addOfferLayout.setVisibility(View.VISIBLE);
                etAddOffer.setEnabled(true);
                etAddOffer.setText("");
                etAddOffer.setHint(getString(R.string.no_content));
                etAddOffer.setHintTextColor(getResources().getColor(R.color.paleRed));
                return;
            } else if (userOffer.equals(SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(round.getSalon_id() + "" + userId)))
            {
                return;
            }

            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                    REQ_SET_USER_OFFER, new Request<>(REQ_SET_USER_OFFER, SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                            , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                            , round.getSalon_id()
                            , userOffer
                            , null, null, null), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            //Save user Offer
                            SharedPrefManager.getInstance(SalonActivity.this).saveUserOffer(String.valueOf(round.getSalon_id() + userId), userOffer);
                            updateLatestActivity(mainObject.get("message").getAsString());

                            displayUserOffer();

                            try
                            {
                                JSONObject obj = new JSONObject();
                                obj.put("user", userName);
                                obj.put("salon_id", round.getSalon_id());
                                socketUtils.emitData("addOffer", obj);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }
                        }

                        @Override
                        public void handleAfterResponse() {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                            etAddOffer.setEnabled(true);
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                            etAddOffer.setEnabled(true);
                            snackBuilder.setSnackText(errorMessage).showSnack();
                        }
                    });
        }
        catch (NumberFormatException e)
        {
            addOfferLayout.setVisibility(View.VISIBLE);
            joinProgress.setVisibility(View.GONE);
            Crashlytics.logException(e);
        }
        etAddOffer.setEnabled(true);
    }

    // region getters
    public Round getRound() {
        return round;
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
    // endregion

    // region tabs
    private void selectTopTenTab() {
        getTopTen();
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
            Common.Instance().hideProgress(topTenRecycler, pbTopTen);
        } else
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
        } else
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

        } else
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
        } else
        {
            initActivityRecycler();
        }

        activityRecycler.scrollToPosition(0);
    }
    // endregion

    // region salon time
    public void getRemainingTimeFromServer() {
        dialogBuilder.displayLoadingDialog();
        Log.d(TAG, "getRemainingTimeFromServer: doing");
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                REQ_GET_SALON_WITH_REALTIME, new Request<>(REQ_GET_SALON_WITH_REALTIME, userId, apiToken, round.getSalon_id()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Log.d("test_salon_time", mainObject.get("isToday").getAsBoolean() + "");
                        if (mainObject.get("isToday").getAsBoolean())
                        { // today ?
                            roundRemainingTime = ParseResponses.parseRoundRemainingTime(mainObject);
                            round.setRound_id(roundRemainingTime.getLast_round_id());
                            initCountDown();
                        } else
                        { // !today
                            tvSalonMessage.setText(round.getMessage());
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        Log.d(TAG, "getRemainingTimeFromServer: done");
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
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
        countDownController.setRoundRemainingTime(roundRemainingTime); // set round remaining time
        if (roundRemainingTime.isUserJoin())
        { // update join state
            joinState = 2;
        } else
        {
            joinState = 0;
        }
    }

    public void checkOnTime() {
        if (roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state() || roundRemainingTime.isFirst_round_state() || roundRemainingTime.isFirst_rest_state() || roundRemainingTime.isSecond_round_state() || roundRemainingTime.isSecond_rest_state())
        {
            socketUtils.connectSocket();

            if (roundRemainingTime.isUserJoin())
            { // member?
                chatUtils.enableChat();
            } else
            { // !member
                chatUtils.disableChat();
            }
        } else
        {
            chatUtils.disableChat();
        }

        if (roundRemainingTime.isUserJoin() && (roundRemainingTime.isFree_join_state() || roundRemainingTime.isPay_join_state()))
        { // on join time & member
            btnLeaveRound.setVisibility(View.VISIBLE); // display leave salon btn
        } else
        { // !join time
            btnLeaveRound.setVisibility(View.GONE); // hide leave salon btn
        }

        if (!roundRemainingTime.isUserJoin() && roundRemainingTime.isPay_join_state())
        { // display golden card layout
            btnJoinRound.setVisibility(View.GONE);
            if (goldenCard != null)
            {
                displayGoldenLayout();
            } else
            {
                Log.d("Golden_card:", "id: null");
            }

        } else
        { // hide golden card layout
            hideGoldenLayout();
        }

        if (roundRemainingTime.isFirst_round_state() || roundRemainingTime.isSecond_round_state())
        {
            topTenRecycler.setVisibility(View.GONE);// hide top ten views
            tvTopTenTab.setVisibility(View.GONE);

            if (roundRemainingTime.isUserJoin())
            { // member ?
                addOfferLayout.setVisibility(View.VISIBLE); // display add offer layout
                displayUserOffer(); // get user last Offer
            }
        } else
        {
            addOfferLayout.setVisibility(View.GONE); // hide add offer layout
        }

        if (roundRemainingTime.isFirst_rest_state() || roundRemainingTime.isSecond_rest_state())
        {
            // clear user offer
            SharedPrefManager.getInstance(SalonActivity.this).clearUserOffer(round.getSalon_id() + "" + userId);
            // display top ten
            tvTopTenTab.setVisibility(View.VISIBLE);
            topTenRecycler.setVisibility(View.VISIBLE);
            selectTopTenTab();
        }

        if (roundRemainingTime.isClose_hall_state() || roundRemainingTime.getRound_state().equals("close"))
        {
            topTenRecycler.setVisibility(View.VISIBLE);
            tvTopTenTab.setVisibility(View.VISIBLE);

            selectTopTenTab();
            getWinner();
            socketUtils.disconnectSocket();
            chatUtils.disableChat();
        }
    }

    private void displayUserOffer() {
        etAddOffer.setHint(String.valueOf(SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(round.getSalon_id() + "" + userId)));
    }

    private void getTopTen() {
        Log.d(TAG, "getTopTen: doing");
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_GET_TOP_TEN, new Request<>(REQ_GET_TOP_TEN, userId, apiToken, round.getSalon_id(), round.getRound_id()
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
        Log.d(TAG, "getWinner: doing");
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_GET_WINNER, new Request<>(REQ_GET_WINNER, userId, apiToken, round.getSalon_id(), round.getRound_id()
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        try
                        {
                            String winnerName = mainObject.get("user_name").getAsString();
                            String message = mainObject.get("message").getAsString();
                            String offer = mainObject.get("offer").getAsString();

                            if (userId == mainObject.get("user_id").getAsInt() && roundRemainingTime.isUserJoin())
                            { // winner ?
                                Intent i = new Intent(SalonActivity.this, WinnerActivity.class);
                                i.putExtra("winner_name", winnerName);
                                i.putExtra("offer", offer);
                                startActivity(i);
                            }

                            Activity activity = new Activity(winnerName + " " + message + offer, "");
                            updateLatestActivity(activity.getBody());
                            updateActivityList(activity);

                        }
                        catch (NullPointerException e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        Log.d(TAG, "getWinner: done");
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
    private void initJoinConfirmationDialog() {
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
        joinAlert = dialogBuilder.create();
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

        // display confirmation layout
        btnJoinRound.setVisibility(View.GONE);
        joinAlert.show();
    }

    private void congratsSubscribing() { // Congratulation Screen to Join Round
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
        joinAlert.dismiss();
    }

    private void onSubscriptionCanceled() {
        joinState = 0;

        // hide confirmation layout
        btnJoinRound.setVisibility(View.VISIBLE);
        joinAlert.dismiss();
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
                REQ_SET_USER_SALON, new Request<>(REQ_SET_USER_SALON
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                        , round.getSalon_id()
                        , String.valueOf(Common.Instance().getCurrentTimeInMillis())
                        , ""
                        , null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        congratsSubscribing();
                        chatUtils.enableChat();
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
                REQ_SET_ROUND_LEAVE, new Request<>(REQ_SET_ROUND_LEAVE, userId, apiToken, round.getSalon_id()
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
            FirebaseMessaging.getInstance().subscribeToTopic("salon_" + round.getSalon_id());
        }
        SharedPrefManager.getInstance(this).saveSubscribedSalonId(round.getSalon_id());
    }

    private void unSubscribeUserFromSalonNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + round.getSalon_id());
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
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvSalonId.setText(String.valueOf(round.getSalon_id()));


        Picasso.get()
                .load(round.getProduct_image())
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
        if (round != null)
            if (round.getProduct_images() != null)
            {
                RecyclerView imagesRecycler = parent.findViewById(R.id.product_details_images_recycler);
                imagesRecycler.setHasFixedSize(true);
                imagesRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false));
                imagesRecycler.setAdapter(new ProductSubImagesAdapter(this, round.getProduct_images()));
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
        tvCategoryLabel.setText(getResources().getString(R.string.category) + ":");
        tvProductName.setText(round.getProduct_name());
        tvProductCategory.setText(round.getCategory_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvProductDescription.setText(round.getProduct_product_description());

        Picasso.get()
                .load(round.getProduct_image())
                .resize(800, 800)
                .onlyScaleDown()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .into(ivProductMainViewer);

        SubImage.setImageUrl(round.getProduct_image());
    }

    public void switchImageVideo(@NonNull String url, Drawable drawable) {
        SubImage.setImageUrl(url);

        if (SubImage.getImageUrl().endsWith(".mp4") || SubImage.getImageUrl().endsWith(".3gp"))
        {

            ivProductMainViewer.setVisibility(View.INVISIBLE);
            vpProductMainVideo.setVisibility(View.VISIBLE);

            setupVideoPlayer(SubImage.getImageUrl());

        } else
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
                } else
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
        for (int i = 0; i < round.getSalon_cards().size(); i++)
        {
            if (round.getSalon_cards().get(i).getCard_type().equals("gold"))
            {
                goldenCard = round.getSalon_cards().get(i);

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

        } else
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
                } else
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
            dialogBuilder.displayLoadingDialog();
            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    REQ_ADD_CARDS_TO_USER, new Request<>(REQ_ADD_CARDS_TO_USER, userId, apiToken, goldenCard.getCard_id()
                            , null, null, null, null), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                            btnUseGoldenCard.setEnabled(false);
                            getUserCardsForSalonFromServer();
                        }

                        @Override
                        public void handleAfterResponse() {
                            dialogBuilder.hideLoadingDialog();
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            dialogBuilder.hideLoadingDialog();
                            snackBuilder.setSnackText(errorMessage).showSnack();
                        }
                    });
        }
    }

    public void useGoldenCard() {
        if (goldenCard != null)
        {
            dialogBuilder.displayLoadingDialog();
            hideGoldenLayout();
            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    REQ_USE_GOLDEN_CARD, new Request<>(REQ_USE_GOLDEN_CARD, userId, apiToken, round.getSalon_id(), goldenCard.getCard_id(), round.getRound_id()
                            , null, null), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                            roundRemainingTime.setUserJoin(true);
                            goldenCard.setCount(goldenCardCount - 1);
                            getUserCardsForSalonFromServer();
                            getRemainingTimeFromServer();

                            congratsSubscribing();
                            joinAlert.show();
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
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        findViewById(R.id.cards_bag_btn_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // just clicked
                if (gestureDetector.onTouchEvent(motionEvent))
                {
                    cardIconClicked();
                }

                // moved
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        // reinitialize points
                        pointerPoint.set(motionEvent.getX(), motionEvent.getY());
                        staringPoint.set(view.getX(), view.getY());

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // move smoothly
                        view.setX((int) (staringPoint.x + motionEvent.getX() - pointerPoint.x));
                        view.setY((int) (staringPoint.y + motionEvent.getY() - pointerPoint.y));
                        staringPoint.set(view.getX(), view.getY());

                        break;
                    case MotionEvent.ACTION_UP:
                        // checks
                        handleWithScreenBorders(view);
                        view.performClick();

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    private void handleWithScreenBorders(View view) {
        // if x of the left border || in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2))
        {
            view.animate().translationX(0).setDuration(250).start();
        }

        // if x of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2))
        {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(250).start();
        }

        // if y of the up border
        if (view.getY() < 0)
        {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if y of the bottom border
        if (view.getY() > (screenHeight - (view.getHeight() / 2)))
        {
            view.animate().translationY(screenHeight - view.getHeight()).setDuration(200).start();
        }
    }

    private void getScreenDimensions() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void cardIconClicked() {
        // open sheet
        if (mBottomSheetDialogCardsBag.isShowing())
        {
            mBottomSheetDialogCardsBag.dismiss();
        } else
        { // close sheet
            mBottomSheetDialogCardsBag.show();
        }
    }

    public void initBottomSheetCardsBag() {
        mBottomSheetDialogCardsBag = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_cards_bag, null);

        // init bottom sheet views
        if (round != null && round.getSalon_cards() != null)
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
                } else
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
                REQ_GET_USER_CARDS_BY_SALON, new Request<>(REQ_GET_USER_CARDS_BY_SALON, userId, apiToken, round.getSalon_id()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        userCards.clear();
                        userCards.addAll(ParseResponses.parseUserCardsBySalon(mainObject));

                        int allUserCardsCount = 0;
                        if (round.getSalon_cards() != null)
                        {
                            for (Card userCard : userCards)
                            {
                                for (Card salonCard : round.getSalon_cards())
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
                            cardsRecycler.setAdapter(new SalonCardsAdapter(SalonActivity.this, round.getSalon_cards(), round.getSalon_id(), round.getRound_id(), salonMainLayout));
                        } else
                        {
                            cardsRecycler.getAdapter().notifyDataSetChanged();
                        }

                        if (userCards.size() > 0)
                        {
                            tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                        } else
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
        if (round != null)
        {
            try
            {
                JSONObject obj = new JSONObject();
                obj.put("user", SharedPrefManager.getInstance(SalonActivity.this).getUser().getName());
                obj.put("salon_id", round.getSalon_id());

                socketUtils.emitData("leaveTyping", obj);
                socketUtils.emitData("leave", obj);
                chatUtils.sendTypingState = true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        socketUtils.disconnectSocket();
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

//    private void initHelp() {
//        //Customers Service
//        Zendesk.INSTANCE.init(getApplicationContext(), "https://itgeeks.zendesk.com",
//                "6d1749c16b1fa13aaf7a96a39614131f8eba1e5d27ed37bb",
//                "mobile_sdk_client_e65a598574b57edaf2e8");
//        Identity identity = new AnonymousIdentity();
//        Zendesk.INSTANCE.setIdentity(identity);
//        Support.INSTANCE.init(Zendesk.INSTANCE);
//    }
    // endregion
}