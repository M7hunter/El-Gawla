package it_geeks.info.gawla_app.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.RoundRealTimeModel;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Controllers.Adapters.BottomCardsAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.ProductSubImagesAdapter;
import it_geeks.info.gawla_app.views.Round.RoundStartToEnd;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();
    RoundStartToEnd roundStartToEnd;
    RoundRealTimeModel roundRealTimeModel;
    public TextView round_notification_text;

    //Attention Joun Screen
    ImageView icon;
    public TextView header, text, tvSalonTime;

    private String product_name, product_image, product_category, category_color, product_price, product_description, round_start_time, round_end_time, first_join_time, second_join_time, round_date, round_time, rest_time;
    int product_id, salon_id;
    String apiToken;
    String userName;
    int userId;
    private List<ProductSubImage> subImageList = new ArrayList<>();
    private List<Card> cardList = new ArrayList<>();

    View salonMainContainer;

    int joinStatus; // 0 // = watcher, 1 = want to join, 2 = joined

    public Button btnJoinRound, btnAddOffer;
    EditText etAddOffer;
    CardView more, notificationCard, confirmationLayout, useRoundCard;
    LinearLayout addOfferLayout , roundTimeCard;
    FrameLayout overlayLayout;
    ProgressBar joinProgress, joinConfirmationProgress;
    ProgressDialog progress;
    private Round round;
    ImageView out_round;
    private BottomSheetDialog mBottomSheetDialogActivateCard;
    private BottomSheetDialog mBottomSheetDialogProductDetails;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;
    private int screenWidth;
    private int screenHeight;

    public ImageView imProductMainImage;
    public VideoView vpProductMainVideo;
    private ImageView btnPlayPause;
    private int stopPosition = 0;

    private Socket socket;

    public String timeState = "";

    ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    public ProductSubImage productSubImage = new ProductSubImage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        userName = SharedPrefManager.getInstance(SalonActivity.this).getUser().getName();

        initViews();

        getRoundData(savedInstanceState);

        initRoundViews_setData();

        screenDimensions();

        initCardsIcon();

        initBottomSheetProductDetails();

        initBottomSheetActivateCards();

        initDivs();

        handleEvents();
    }

    public void initSocket() {
        try {
            // http://dev.itgeeks.info:8888
            socket = IO.socket("http://dev.itgeeks.info:8888");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();

        try {
            JSONObject o = new JSONObject();
            o.put("room", salon_id);
            socket.emit("joinRoom", o);
        } catch (JSONException e) {

        }

        socket.emit("user_join", userName);

        socket.on("new_member", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayRoundNotification(args[0].toString());
            }
        }).on("member_add_offer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayRoundNotification(args[0].toString());
            }
        }).on("member_leave", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayRoundNotification(args[0].toString());
            }
        });
    }

    private void displayRoundNotification(String notificationMsg) {
        try {
            round_notification_text.setText(notificationMsg);
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(SalonActivity.this, "index!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(SalonActivity.this, "!!", Toast.LENGTH_SHORT).show();
        }
    }

    // loading screen
    public void setLoadingScreen() {
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setCancelable(false);
        progress.show();
    }

    public void closeLoadingScreen() {
        progress.dismiss();
    }

    public void getRealtimeOfRound() {
        setLoadingScreen();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "getSalonWithRealTime", new Request(userId, apiToken, salon_id), new HandleResponses() {

            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                closeLoadingScreen();

                boolean isToday = mainObject.get("isToday").getAsBoolean();
                if (isToday) {
                    startTimeDown(ParseResponses.parseRoundRealTime(mainObject));
                } else {
                    tvSalonTime.setText(getResources().getString(R.string.round_date)+"\n" + round_date);
                    tvSalonTime.setTextSize(20);
                }

            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {

            }

            @Override
            public void handleEmptyResponse() {
                closeLoadingScreen();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                closeLoadingScreen();
                Snackbar.make(findViewById(R.id.salon_main_layout), R.string.connection_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRealtimeOfRound();
                    }
                }).show();
            }
        });
    }

    public View getSnackBarContainer() {
        if (salonMainContainer == null) {
            salonMainContainer = findViewById(R.id.salon_main_layout);
        }
        return salonMainContainer;
    }

    private void initDivs() {
        /// for timedown TODO TimeDown View Init
        for (int i = 1; i <= 12; i++) {
            String divUpID = "div_up" + i;
            int resDivIDUp = getResources().getIdentifier(divUpID, "id", getPackageName());
            upDivsList.add((ImageView) findViewById(resDivIDUp));

            String divDownID = "div_down" + i;
            int resDivIDDown = getResources().getIdentifier(divDownID, "id", getPackageName());
            downDivsList.add((ImageView) findViewById(resDivIDDown));
        }
        for (int i = 0; i < 12; i++) {

            String divUpNum = "digit_" + i + "_upper";
            int resdivUpNum = getResources().getIdentifier(divUpNum, "drawable", getPackageName());
            drawablesUp.add(resdivUpNum);

            String divDownNum = "digit_" + i + "_lower";
            int resdivDownNum = getResources().getIdentifier(divDownNum, "drawable", getPackageName());
            drawablesDown.add(resdivDownNum);

        }
    }

    //Round Start
    private void startTimeDown(RoundRealTimeModel roundRealTimeModel) {
        this.roundRealTimeModel = roundRealTimeModel;
        if (roundRealTimeModel.isUserJoin()) {
            joinStatus = 2;
        } else {
            joinStatus = 0;
        }

        RoundStartToEndModel roundStartToEndModel = new RoundStartToEndModel(upDivsList, downDivsList, drawablesUp, drawablesDown);
        roundStartToEnd = new RoundStartToEnd(SalonActivity.this, roundStartToEndModel);
        roundStartToEnd.setJoinStatus(joinStatus);  // User Status From Server to time
        roundStartToEnd.setTime(roundRealTimeModel);// set round time
        try {
            roundStartToEnd.start();
        } catch (NullPointerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (timeState.equals("first_round_status") || timeState.equals("second_round_status")) {
            initSocket();
        }
    }

    private void getRoundData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) { // get data from previous page
                product_id = extras.getInt("product_id");
                salon_id = extras.getInt("salon_id");
                product_name = extras.getString("product_name");
                product_category = extras.getString("category_name");
                category_color = extras.getString("category_color");
                product_price = extras.getString("product_commercial_price");
                product_description = extras.getString("product_product_description");
                product_image = extras.getString("product_image");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");
                first_join_time = extras.getString("first_join_time");
                second_join_time = extras.getString("second_join_time");
                round_date = extras.getString("round_date");
                round_time = extras.getString("round_time");
                rest_time = extras.getString("rest_time");
                subImageList = (List<ProductSubImage>) extras.getSerializable("product_images");
                cardList = (List<Card>) extras.getSerializable("salon_cards");

                getRealtimeOfRound();

                round = new Round(product_id,
                        salon_id,
                        product_name,
                        product_category,
                        category_color,
                        extras.getString("country_name"),
                        product_price,
                        product_description,
                        product_image,
                        subImageList,
                        cardList,
                        round_start_time,
                        round_end_time,
                        first_join_time,
                        second_join_time,
                        round_date,
                        round_time,
                        rest_time);
            }

        } else { // get data from saved state
            product_id = savedInstanceState.getInt("product_id");
            salon_id = savedInstanceState.getInt("salon_id");
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_category = (String) savedInstanceState.getSerializable("category_name");
            category_color = (String) savedInstanceState.getSerializable("category_color");
            product_price = (String) savedInstanceState.getSerializable("product_commercial_price");
            product_description = (String) savedInstanceState.getSerializable("product_product_description");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");
            first_join_time = (String) savedInstanceState.getSerializable("first_join_time");
            second_join_time = (String) savedInstanceState.getSerializable("second_join_time");
            round_date = (String) savedInstanceState.getSerializable("round_date");
            round_time = (String) savedInstanceState.getSerializable("round_time");
            rest_time = (String) savedInstanceState.getSerializable("rest_time");
            subImageList = (List<ProductSubImage>) savedInstanceState.getSerializable("product_images");
            cardList = (List<Card>) savedInstanceState.getSerializable("salon_cards");

            round = new Round(product_id,
                    salon_id,
                    product_name,
                    product_category,
                    category_color,
                    (String) savedInstanceState.getSerializable("country_name"),
                    product_price,
                    product_description,
                    product_image,
                    subImageList,
                    cardList,
                    round_start_time,
                    round_end_time,
                    first_join_time,
                    second_join_time,
                    round_date,
                    round_time,
                    rest_time);
        }
    }

    private void initRoundViews_setData() {
        TextView tvProductName, tvProductPrice, salonId;
        ImageView imProductImage;
        // init views
        tvSalonTime = findViewById(R.id.salon_time);
        tvProductName = findViewById(R.id.salon_round_product_name);
        tvProductPrice = findViewById(R.id.salon_round_product_price);
        imProductImage = findViewById(R.id.salon_round_product_image);
        salonId = findViewById(R.id.salon_number);
        round_notification_text = findViewById(R.id.round_notification_text);

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        salonId.setText(String.valueOf(round.getSalon_id()));

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.placeholder).into(imProductImage);
    }

    public void initViews() {
        progress = new ProgressDialog(this);
        overlayLayout = findViewById(R.id.overlay_layout);
        more = findViewById(R.id.more);
        btnJoinRound = findViewById(R.id.btn_join_round);
        etAddOffer = findViewById(R.id.add_offer_et);
        confirmationLayout = findViewById(R.id.join_confirmation_layout);
        useRoundCard = findViewById(R.id.use_round_card);
        notificationCard = findViewById(R.id.round_notification_card);
        btnAddOffer = findViewById(R.id.add_offer_btn);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        roundTimeCard = findViewById(R.id.roundTimeCard);
        joinProgress = findViewById(R.id.join_progress);
        joinConfirmationProgress = findViewById(R.id.join_confirmation_progress);
        apiToken = Common.Instance(SalonActivity.this).removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();

        icon = findViewById(R.id.join_confirmation_icon);
        header = findViewById(R.id.join_confirmation_header);
        text = findViewById(R.id.join_confirmation_text);

        // notification icon
        findViewById(R.id.salon_notification_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonActivity.this, NotificationActivity.class));
            }
        });
        // Leave Round
        out_round = findViewById(R.id.out_round);
        out_round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOutRound();
            }
        });
    }

    private void userOutRound() {
        AlertDialog.Builder alertOut = new AlertDialog.Builder(SalonActivity.this);
        alertOut.setMessage("Are you sure to leave this Salon ?.");
        alertOut.setPositiveButton("Logout Me", outRound);
        alertOut.setNegativeButton("Cancel", null);
        alertOut.setCancelable(false);
        alertOut.create();
        alertOut.show();
    } // check if user keep wanting out .

    private DialogInterface.OnClickListener outRound = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "setRoundLeave", new Request(userId, apiToken, salon_id), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {
                    roundStartToEnd.stop(); // stop Time Down
                    stopSalonNotification();
                    getRealtimeOfRound();
                    attentionScreen();
                }

                @Override
                public void handleFalseResponse(JsonObject errorObject) {

                }

                @Override
                public void handleEmptyResponse() {

                }

                @Override
                public void handleConnectionErrors(String errorMessage) {
                    Snackbar.make(findViewById(R.id.salon_main_layout), R.string.connection_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getRealtimeOfRound();
                        }
                    }).show();
                }
            });
        }
    };

    private void handleEvents() {
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (joinStatus) {
                    case 0:
                        displayConfirmationLayout();
                        break;
                    case 1:
                        addUserToSalon();
                        break;
                    case 2:
                        hideConfirmationLayout();
                        round_notification_text.setText("You are joined .");
                        out_round.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        // open product details sheet
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialogProductDetails.isShowing()) { // close sheet
                    mBottomSheetDialogProductDetails.dismiss();
                } else {
                    mBottomSheetDialogProductDetails.show();
                }
            }
        });

        // cancel confirmation
        overlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinStatus == 2) {
                    hideConfirmationLayout();
                    out_round.setVisibility(View.VISIBLE);
                } else if (roundRealTimeModel.isUserJoin()) {
                    out_round.setVisibility(View.VISIBLE);
                } else {
                    cancelConfirmation();
                }
            }
        });

        //
        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundRealTimeModel.isUserJoin()) {
                    // switch
                    if (btnAddOffer.getText().toString().equals(getString(R.string.add_deal))) {
                        etAddOffer.setEnabled(false);
                        btnAddOffer.setText(getResources().getString(R.string.edit));
                        btnAddOffer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        sendOfferToServer();

                    } else if (btnAddOffer.getText().toString().equals(getString(R.string.edit))) {
                        etAddOffer.setEnabled(true);
                        btnAddOffer.setText(getResources().getString(R.string.add_deal));
                        btnAddOffer.setBackgroundColor(getResources().getColor(R.color.greenBlue));
                    }
                }
            }
        });
    }

    private void addUserToSalon() {
        joinConfirmationProgress.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                "setUserSalon",
                new Request(SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                        , String.valueOf(Common.Instance(SalonActivity.this).getCurrentTimeInMillis())
                        , ""
                        , salon_id), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        // Notification
                        changeConfirmationState();
                        out_round.setVisibility(View.VISIBLE);
                        joinStatus = 2;
                        startSalonNotification();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject errorObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        joinConfirmationProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        joinConfirmationProgress.setVisibility(View.GONE);
                    }
                });
    }

    // start Notification To This Salon
    public void startSalonNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("salon_" + salon_id);
    }

    // stop Notification To This Salon
    public void stopSalonNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + salon_id);
    }

    private void sendOfferToServer() {
        addOfferLayout.setVisibility(View.GONE);
        joinProgress.setVisibility(View.VISIBLE);
        try {
            final int userOffer = Integer.parseInt(etAddOffer.getText().toString());

            if (String.valueOf(userOffer).isEmpty() || userOffer == 0) {
                joinProgress.setVisibility(View.GONE);
                addOfferLayout.setVisibility(View.VISIBLE);
                etAddOffer.setText("");
                etAddOffer.setHint(getString(R.string.no_content));
                etAddOffer.setHintTextColor(getResources().getColor(R.color.paleRed));
                return;
            }

            // test
//            socket.emit("addOffer", userName);
//            joinProgress.setVisibility(View.GONE);
//            addOfferLayout.setVisibility(View.VISIBLE);

            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                    "setUserOffer",
                    new Request(SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                            , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                            , salon_id
                            , userOffer), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            round_notification_text.setText(mainObject.get("message").getAsString());
                            socket.emit("addOffer", userName);
                        }

                        @Override
                        public void handleFalseResponse(JsonObject errorObject) {

                        }

                        @Override
                        public void handleEmptyResponse() {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                            Toast.makeText(SalonActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NumberFormatException e) {
            joinProgress.setVisibility(View.GONE);
            addOfferLayout.setVisibility(View.VISIBLE);
        }
    }

    // join events
    private void displayConfirmationLayout() {
        joinStatus = 1;
        roundStartToEnd.setJoinStatus(joinStatus);

        // hide background views
        more.setVisibility(View.GONE);
        notificationCard.setVisibility(View.GONE);

        // display confirmation layout
        confirmationLayout.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
    }

    private void changeConfirmationState() {
        joinStatus = 2;
        roundStartToEnd.setJoinStatus(joinStatus);
        congratulationScreen();
    }

    private void attentionScreen() { // Attention Screen  to Join Round
        icon.setImageDrawable(getResources().getDrawable(R.drawable.q_mark_in_circle));
        header.setText(getString(R.string.Attention));
        header.setTextColor(getResources().getColor(R.color.midBlue));
        text.setText(getString(R.string.Attention_Details));
        btnJoinRound.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnJoinRound.setText(getString(R.string.join_round));
    }

    private void congratulationScreen() { // Congratulation Screen to Join Round
        icon.setImageDrawable(getResources().getDrawable(R.drawable.joinedrounddone));
        header.setText(getString(R.string.Congratulations_Attention));
        header.setTextColor(getResources().getColor(R.color.greenBlue));
        text.setText(getString(R.string.Congratulations_Attention_Details));
        btnJoinRound.setBackgroundColor(getResources().getColor(R.color.greenBlue));
        btnJoinRound.setText(getString(R.string.start_play));
    }

    public void hideConfirmationLayout() {
        // display background views
        more.setVisibility(View.VISIBLE);
        notificationCard.setVisibility(View.VISIBLE);

        // hide confirmation layout
        confirmationLayout.setVisibility(View.GONE);
        btnJoinRound.setVisibility(View.GONE);
        overlayLayout.setVisibility(View.GONE);
        addOfferLayout.setVisibility(View.GONE);
        useRoundCard.setVisibility(View.GONE);

        if (roundRealTimeModel.isOpen_hall_status() || roundRealTimeModel.isClose_hall_status()) {
            notificationCard.setVisibility(View.GONE);
        }

        if (roundRealTimeModel.isFirst_round_status() && roundRealTimeModel.isUserJoin() || roundRealTimeModel.isSeconed_round_status() && roundRealTimeModel.isUserJoin()) {
            addOfferLayout.setVisibility(View.VISIBLE);
        }

        if (roundRealTimeModel.isFree_join_status() && roundRealTimeModel.isUserJoin() || roundRealTimeModel.isPay_join_status() && roundRealTimeModel.isUserJoin()) {
            out_round.setVisibility(View.VISIBLE);
        } else {
            out_round.setVisibility(View.GONE);
        }

        if (roundRealTimeModel.isPay_join_status() && !roundRealTimeModel.isUserJoin()) {
            useRoundCard.setVisibility(View.VISIBLE);
        }


    }

    public void cancelConfirmation() {
        joinStatus = 0;

        // display background views
        more.setVisibility(View.VISIBLE);
        notificationCard.setVisibility(View.VISIBLE);

        // hide confirmation layout
        confirmationLayout.setVisibility(View.GONE);
        overlayLayout.setVisibility(View.GONE);
    }

    // cards bag
    private void initCardsIcon() {
        RelativeLayout cardsIconContainer = findViewById(R.id.cards_bag_btn_container);
        cardsIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
    }

    private void initBottomSheetActivateCards() {
        mBottomSheetDialogActivateCard = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_active_cards, null);

        //init bottom sheet views
        if (cardList != null) {
            RecyclerView cardsRecycler = sheetView.findViewById(R.id.salon_cards_bottom_recycler);
            cardsRecycler.setHasFixedSize(true);
            cardsRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));
            cardsRecycler.setAdapter(new BottomCardsAdapter(SalonActivity.this, cardList));
        }

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_activate_cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogActivateCard.isShowing()) {
                    mBottomSheetDialogActivateCard.dismiss();

                } else {
                    mBottomSheetDialogActivateCard.show();
                }
            }
        });

        //
        mBottomSheetDialogActivateCard.setContentView(sheetView);
        Common.Instance(SalonActivity.this).setBottomSheetHeight(sheetView);
        mBottomSheetDialogActivateCard.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    // product details
    private void initBottomSheetProductDetails() {
        mBottomSheetDialogProductDetails = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);

        //init bottom sheet views
        bottomSubImagesRecycler(sheetView);
        bottomViews_setDetails(sheetView);

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_product_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogProductDetails.isShowing()) {
                    mBottomSheetDialogProductDetails.dismiss();

                } else {
                    mBottomSheetDialogProductDetails.show();
                }
            }
        });

        //
        mBottomSheetDialogProductDetails.setContentView(sheetView);
        Common.Instance(SalonActivity.this).setBottomSheetHeight(sheetView);
        mBottomSheetDialogProductDetails.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void bottomSubImagesRecycler(View parent) {
        if (subImageList != null) {
            RecyclerView imagesRecycler = parent.findViewById(R.id.product_details_images_recycler);
            imagesRecycler.setHasFixedSize(true);
            imagesRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false));
            imagesRecycler.setAdapter(new ProductSubImagesAdapter(this, subImageList));
        }
    }

    private void bottomViews_setDetails(View parent) {
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductDescription, tvCategoryLabel;
        // init views
        tvProductName = parent.findViewById(R.id.product_details_name);
        tvProductCategory = parent.findViewById(R.id.product_details_category);
        tvProductPrice = parent.findViewById(R.id.product_details_price);
        tvProductDescription = parent.findViewById(R.id.product_details_descriptions);
        tvCategoryLabel = parent.findViewById(R.id.tv_category_label);
        imProductMainImage = parent.findViewById(R.id.product_details_main_image);
        vpProductMainVideo = parent.findViewById(R.id.player);
        btnPlayPause = parent.findViewById(R.id.btn_play_pause);

        // set data
        tvCategoryLabel.setText(getResources().getString(R.string.category) + ":");
        tvProductName.setText(round.getProduct_name());
        tvProductCategory.setText(round.getCategory_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvProductDescription.setText(round.getProduct_product_description());
        Picasso.with(SalonActivity.this)
                .load(round.getProduct_image())
                .placeholder(R.drawable.placeholder)
                .into(imProductMainImage);

        productSubImage.setImageUrl(round.getProduct_image());
    }

    public void switchImageVideo(@NonNull String url) {
        productSubImage.setImageUrl(url);

        if (productSubImage.getImageUrl().endsWith(".mp4") || productSubImage.getImageUrl().endsWith(".3gp")) {

            imProductMainImage.setVisibility(View.INVISIBLE);
            vpProductMainVideo.setVisibility(View.VISIBLE);

            setupVideoPlayer(productSubImage.getImageUrl());

        } else {
            vpProductMainVideo.setVisibility(View.INVISIBLE);
            imProductMainImage.setVisibility(View.VISIBLE);

            Picasso.with(SalonActivity.this).load(productSubImage.getImageUrl()).placeholder(R.drawable.gawla_logo_blue).into(imProductMainImage);
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
                if (btnPlayPause.getAlpha() == 1) {
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
                if (btnPlayPause.getAlpha() == 0) {
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
                if (vpProductMainVideo.isPlaying()) {
                    if (vpProductMainVideo.canPause()) {
                        stopPosition = vpProductMainVideo.getCurrentPosition();
                        vpProductMainVideo.pause();
                        btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

                        handler.removeCallbacks(runnable);
                    }
                } else {
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

    // freedom
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // just clicked
        if (gestureDetector.onTouchEvent(motionEvent)) {
            cardIconClicked();
        }

        // moved
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // move smoothly
                view.setX((int) (staringPoint.x + motionEvent.getX() - pointerPoint.x));
                view.setY((int) (staringPoint.y + motionEvent.getY() - pointerPoint.y));
                staringPoint.set(view.getX(), view.getY());

                break;
            case MotionEvent.ACTION_DOWN:
                // reinitialize points
                pointerPoint.set(motionEvent.getX(), motionEvent.getY());
                staringPoint.set(view.getX(), view.getY());

                break;
            case MotionEvent.ACTION_UP:
                // checks
                handleWithScreenBorders(view);

                break;
            default:
                break;
        }

        return true;
    }

    private void handleWithScreenBorders(View view) {
        // if out of the left border || in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2)) {
            view.animate().translationX(0).setDuration(250).start();
        }

        // if out of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2)) {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(250).start();
        }

        // if out of the up border
        if (view.getY() < 0) {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if out of the bottom border
        if (view.getY() > (screenHeight - (view.getHeight() / 2))) {
            view.animate().translationY(screenHeight - view.getHeight()).setDuration(200).start();
        }
    }

    private void screenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void cardIconClicked() {
        // open sheet
        if (mBottomSheetDialogActivateCard.isShowing()) {
            mBottomSheetDialogActivateCard.dismiss();
        } else { // close sheet
            mBottomSheetDialogActivateCard.show();
        }
    }

    // help to separate click from touch
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            roundStartToEnd.stop(); // stop Time Down
        } catch (Exception e) {
        }

        Intent i = new Intent();
        setResult(RESULT_OK, i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionChangeReceiver);
        if (socket != null) {
            if (socket.connected()) {
                socket.emit("leave", userName);
                socket.emit(Socket.EVENT_DISCONNECT, userName);
                socket.disconnect();
            }
        }
        try {
            roundStartToEnd.stop(); // stop Time Down
        } catch (Exception e) {
        }
    }
}