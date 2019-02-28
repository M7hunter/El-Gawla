package it_geeks.info.gawla_app.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import it_geeks.info.gawla_app.Controllers.Adapters.ActivityAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.ChatAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.TopTenAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Activity;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.ChatModel;
import it_geeks.info.gawla_app.Repositry.Models.TopTen;
import it_geeks.info.gawla_app.Repositry.SocketConnection.SocketConnection;
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
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.views.Round.RoundStartToEnd;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();
    RoundStartToEnd roundStartToEnd;
    RoundRealTimeModel roundRealTimeModel;
    public TextView tvRoundActivity;

    //Attention Join Screen
    ImageView joinIcon;
    public TextView joinHeader, joinText, tvSalonTime;

    private String product_name, product_image, product_category, category_color, product_price, product_description, round_start_time, round_end_time, first_join_time, second_join_time, round_date, round_time, rest_time, round_message;
    int product_id, salon_id;
    String apiToken;
    String userName;
    public int userId;
    private List<ProductSubImage> subImageList = new ArrayList<>();
    public List<Card> cardList = new ArrayList<>();
    private List<ChatModel> chatList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private List<Card> userCards = new ArrayList<>();
    RecyclerView chatRecycler, activityRecycler;

    View salonMainContainer;

    int joinStatus; // 0 = watcher, 1 = want to join, 2 = joined

    public Button btnJoinRound, btnAddOffer;
    EditText etAddOffer;
    public CardView more, notificationCard, useRoundCard, activityContainer, chatContainer, topTenContainer;
    LinearLayout addOfferLayout, roundTimeCard, detailsContainer;
    ProgressBar joinProgress, joinConfirmationProgress;
    private Round round;
    TextView btn_leave_round;
    private BottomSheetDialog mBottomSheetDialogActivateCard;
    private BottomSheetDialog mBottomSheetDialogProductDetails;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;
    private int screenWidth;
    private int screenHeight;
    ImageView imgNotification;

    public ImageView imProductMainImage;
    public VideoView vpProductMainVideo;
    private ImageView btnPlayPause;
    private int stopPosition = 0;

    private CardView loadingCard;

    private Socket mSocket;

    public String timeState = "";
    private int goldenCardCount = 0;

    ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    public ProductSubImage productSubImage = new ProductSubImage();

    private AlertDialog joinAlert;
    private Button btnJoinConfirmation, btnUseGoldenCard;
    private RecyclerView topTenRecycler;

    private TextView tvProductDetailsTab, tvSalonActivityTab, tvChatTab, tvTopTenTab, tvChatEmptyHint, tvCardsCount, tvGoldenCardText;
    private View vGoldenCard;
    TextView txEmptyPage;

    private Card goldenCard = new Card();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        userName = SharedPrefManager.getInstance(SalonActivity.this).getUser().getName();

        initViews();

        getRoundData(savedInstanceState);

        initJoinConfirmationDialog();

        connectSocket();

        initRoundViews_setData();

        screenDimensions();

        initCardsIcon();

        initDivs();

        initChat();

        handleEvents();
    }

    private void initActivityRecycler() {
        if (activityList.size() == 0) {
//            txEmptyPage.setText(getString(R.string.activiy_empty));
            txEmptyPage.setVisibility(View.VISIBLE);
            activityRecycler.setVisibility(View.GONE);
        } else {
            txEmptyPage.setVisibility(View.GONE);
            activityRecycler.setVisibility(View.VISIBLE);
            activityRecycler.setHasFixedSize(true);
            activityRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            activityRecycler.setAdapter(new ActivityAdapter(activityList));
            activityRecycler.scrollToPosition(activityList.size() - 1);
        }

    }

    public void initViews() {
        loadingCard = findViewById(R.id.loading_card);
        joinProgress = findViewById(R.id.join_progress);
        more = findViewById(R.id.more);
        topTenRecycler = findViewById(R.id.top_ten_recycler);
        tvCardsCount = findViewById(R.id.tv_cards_count);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        etAddOffer = findViewById(R.id.add_offer_et);
        btnAddOffer = findViewById(R.id.add_offer_btn);
        btnJoinRound = findViewById(R.id.btn_join_round);
        useRoundCard = findViewById(R.id.use_round_card);
        tvGoldenCardText = findViewById(R.id.tv_golden_card_text);
        vGoldenCard = findViewById(R.id.v_golden_card);
        btnUseGoldenCard = findViewById(R.id.btn_use_golden_card);
        tvChatEmptyHint = findViewById(R.id.tv_chat_empty_hint);
        notificationCard = findViewById(R.id.round_notification_card);
        roundTimeCard = findViewById(R.id.roundTimeCard);

        tvTopTenTab = findViewById(R.id.tv_top_ten);
        tvProductDetailsTab = findViewById(R.id.tv_product_details);
        tvSalonActivityTab = findViewById(R.id.tv_salon_activity);
        tvChatTab = findViewById(R.id.tv_salon_chat);

        topTenContainer = findViewById(R.id.top_ten_container);
        detailsContainer = findViewById(R.id.details_container);
        activityContainer = findViewById(R.id.activity_container);
        activityRecycler = findViewById(R.id.salon_activity_recycler);
        chatContainer = findViewById(R.id.chat_container);

        txEmptyPage = findViewById(R.id.txt_page_empty);

        apiToken = Common.Instance(SalonActivity.this).removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();

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
                selectChatTab();
            }
        });


        //Notification icon
        imgNotification = findViewById(R.id.Notification);

        // notification status LiveData
        NotificationStatus.notificationStatus(this, imgNotification);

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonActivity.this, NotificationActivity.class));
            }
        });

        // back
        findViewById(R.id.salon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Leave Round
        btn_leave_round = findViewById(R.id.btn_leave_round);
        btn_leave_round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOutRound();
            }
        });

        // open activity
        findViewById(R.id.activity_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityTab();
            }
        });
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

                getRemainingTimeOfRound();

                round = new Round(product_id,
                        salon_id,
                        extras.getInt("salon_id"),
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
                        rest_time,
                        extras.getBoolean("round_status"),
                        extras.getString("round_message"));
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
                    savedInstanceState.getInt("salon_id"),
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
                    rest_time,
                    savedInstanceState.getBoolean("round_status"),
                    savedInstanceState.getString("round_message"));
        }

        initBottomSheetProductDetails();

        initBottomSheetActivateCards();
    }

    private void calculateGoldenCard() {
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).getCard_type().equals("gold")) {
                goldenCard = cardList.get(i);
                Log.d("Golden_card", "id: " + cardList.get(i).getCard_id());

                goldenCardCount = 0;
                for (Card card : userCards) {
                    if (card.getCard_type().equals(goldenCard.getCard_type())) {
                        goldenCardCount = card.getCount();
                        goldenCard.setCount(card.getCount());
                    }
                }

                initGoldenCardView();
                break;
            }
        }
    }

    private void initGoldenCardView() {
        Common.Instance(this).changeDrawableViewColor(vGoldenCard, goldenCard.getCard_color());
        tvGoldenCardText.setText(goldenCard.getCard_details());

        if (goldenCardCount > 0) {
            btnUseGoldenCard.setText(R.string.use);
            btnUseGoldenCard.setBackgroundColor(getResources().getColor(R.color.greenBlue));

        } else {
            btnUseGoldenCard.setText(R.string.buy_card);
            btnUseGoldenCard.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        btnUseGoldenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goldenCardCount > 0) {
                    useGoldenCard();
                } else {
                    buyGoldenCard();
                }
            }
        });
    }

    private void buyGoldenCard() {
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "addCardsToUser", new Request(userId, apiToken, goldenCard.getCard_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(SalonActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                initBottomSheetActivateCards();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
            }
        });
    }

    private void useGoldenCard() {
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "useGoldenCard", new Request(userId, apiToken, goldenCard.getCard_id(), salon_id, round.getRound_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(SalonActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                initBottomSheetActivateCards();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {

            }
        });
    }

    private void getTopTen() {
        displayLoading();
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getTopTen", new Request(userId, apiToken, salon_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                // Toast.makeText(SalonActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                try {
                    initTopTenRecycler(ParseResponses.parseTopTen(mainObject));
                } catch (Exception e) {
                    Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
            }
        });
    }

    private void initTopTenRecycler(List<TopTen> topTens) {
        if (topTens.size() == 0) {
            txEmptyPage.setVisibility(View.VISIBLE);
            txEmptyPage.setText(getString(R.string.top_ten_empty));
            topTenRecycler.setVisibility(View.GONE);
        } else {
            txEmptyPage.setVisibility(View.GONE);
            topTenRecycler.setVisibility(View.VISIBLE);
            topTenRecycler.setHasFixedSize(true);
            topTenRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            topTenRecycler.setAdapter(new TopTenAdapter(topTens));
        }
    }

    private void selectTopTenTab() {
        getTopTen();
        detailsContainer.setVisibility(View.GONE);
        activityContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.VISIBLE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));

        // text color
        tvProductDetailsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSalonActivityTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvChatTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTopTenTab.setTextColor(Color.WHITE);
    }

    public void selectDetailsTab() {
        detailsContainer.setVisibility(View.VISIBLE);
        activityContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.GONE);

        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));

        // text color
        tvProductDetailsTab.setTextColor(Color.WHITE);
        tvSalonActivityTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvChatTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectActivityTab() {
        getSalonActivityData();
        detailsContainer.setVisibility(View.GONE);
        activityContainer.setVisibility(View.VISIBLE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.GONE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));

        // text color
        tvProductDetailsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSalonActivityTab.setTextColor(Color.WHITE);
        tvChatTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectChatTab() {
        detailsContainer.setVisibility(View.GONE);
        activityContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.VISIBLE);
        topTenContainer.setVisibility(View.GONE);

        if (chatList.size() > 0) {
            tvChatEmptyHint.setVisibility(View.GONE);
        } else {
            tvChatEmptyHint.setVisibility(View.VISIBLE);
        }

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));

        // text color
        tvProductDetailsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSalonActivityTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvChatTab.setTextColor(Color.WHITE);
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void getSalonActivityData() {

        //TODO Will Delete Later
        //        displayLoading();
        //        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getAllActivity", new Request(userId, apiToken, salon_id), new HandleResponses() {
        //            @Override
        //            public void handleTrueResponse(JsonObject mainObject) {
        //                activityList.addAll(ParseResponses.parseSalonActivity(mainObject));
        //                initActivityRecycler();
        //            }
        //
        //            @Override
        //            public void handleFalseResponse(JsonObject errorObject) {
        //
        //            }
        //
        //            @Override
        //            public void handleEmptyResponse() {
        //                hideLoading();
        //            }
        //
        //            @Override
        //            public void handleConnectionErrors(String errorMessage) {
        //                hideLoading();
        //            }
        //        });

    }

    private void initChat() {
        chatRecycler = findViewById(R.id.chat_list);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));
        chatRecycler.setAdapter(new ChatAdapter(SalonActivity.this, chatList));

        final EditText etChatMessage = findViewById(R.id.et_chat_message);

        findViewById(R.id.chat_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinStatus == 2 && !roundRealTimeModel.getRound_status().equals("close")) {
                    if (etChatMessage.getText().toString().trim().isEmpty()) {
                        etChatMessage.setError("Input Empty");
                    } else {
                        JSONObject chatData = new JSONObject();
                        final String message = etChatMessage.getText().toString();
                        try {
                            chatData.put("user_id", SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id());
                            chatData.put("user_name", SharedPrefManager.getInstance(SalonActivity.this).getUser().getName());
                            chatData.put("message", message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSocket.emit("newMessage", chatData);
                        etChatMessage.setText("");
                    }
                } else if (roundRealTimeModel.getRound_status().equals("close")) {
                    Toast.makeText(SalonActivity.this, "Round Closed", Toast.LENGTH_SHORT).show();
                } else if (joinStatus != 2) {
                    Toast.makeText(SalonActivity.this, "You Are Not Join", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                SalonActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject main = (JSONObject) args[0];
                        int user_id;
                        String user_name;
                        String message;
                        String date;
                        try {
                            JSONObject data = main.getJSONObject("message");
                            user_id = data.getInt("user_id");
                            user_name = data.getString("user_name");
                            message = data.getString("message");
                            date = data.getString("date");
                            addMessageToChat(user_id, user_name, message,date);
                            tvChatEmptyHint.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            Log.e("", e.getMessage());
                            return;
                        }
                    }
                });
            }
        });
    }

    private void handleEvents() {
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConfirmationLayout();
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
        joinAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (joinStatus == 2) {
                    hideConfirmationLayout();
                    btn_leave_round.setVisibility(View.VISIBLE);
                    notificationCard.setVisibility(View.VISIBLE);
                } else if (roundRealTimeModel.isUserJoin()) {
                    btn_leave_round.setVisibility(View.VISIBLE);
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
                    etAddOffer.setEnabled(false);
                    sendOfferToServer();
                }
            }
        });
    }

    public void connectSocket() {
        mSocket = new SocketConnection().getSocket();
        mSocket.connect();
    }

    private void intiSocket() {
        try {
            JSONObject o = new JSONObject();
            o.put("room", salon_id);
            o.put("user", userName);
            mSocket.emit("joinRoom", o);
        } catch (JSONException e) {
        }

        mSocket.on("new_member", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).on("member_add_offer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).on("member_leave", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).on("winner", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSocket.emit("allActivity", salon_id); // what action triggers this emit ?!
        mSocket.on("activity", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray main = (JSONArray) args[0];
                            for (int i = 0; i < main.length(); i++) {
                                JSONObject jsonObject = main.getJSONObject(i);
                                activityList.add(new Activity(jsonObject.get("activity").toString(), jsonObject.get("created_at").toString()));
                            }
                            initActivityRecycler();
                        } catch (Exception e) {
                            Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void displayRoundActivity(String notificationMsg) {
        try {
            tvRoundActivity.setText(notificationMsg);
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(SalonActivity.this, "index!", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void getRemainingTimeOfRound() {
        displayLoading();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "getSalonWithRealTime", new Request(userId, apiToken, salon_id), new HandleResponses() {

            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                boolean isToday = mainObject.get("isToday").getAsBoolean();
                if (isToday) {
                    startTimeDown(ParseResponses.parseRoundRealTime(mainObject));
                } else {
                    tvSalonTime.setText(getResources().getString(R.string.round_date) + "\n" + round_date);
                    tvSalonTime.setTextSize(20);
                }
            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
                Snackbar.make(findViewById(R.id.salon_main_layout), R.string.connection_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRemainingTimeOfRound();
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
    }

    private void getWinner() {
        displayLoading();
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getWinner", new Request(userId, apiToken, salon_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                try {
                    if (!mainObject.get("winner").getAsString().equals("")) {

                        String winnerName = mainObject.get("user_name").getAsString();
                        String message = mainObject.get("message").getAsString();
                        String offer = mainObject.get("offer").getAsString();

                        if (userId == mainObject.get("user_id").getAsInt() && roundRealTimeModel.isUserJoin()) { // winner ?
                            Intent i = new Intent(SalonActivity.this, WinnerActivity.class);
                            i.putExtra("winner_name", winnerName);
                            i.putExtra("offer", offer);
                            startActivity(i);
                        } else if (userId != mainObject.get("user_id").getAsInt() && roundRealTimeModel.isUserJoin()) { // !winner
                            new AlertDialog.Builder(SalonActivity.this).setMessage(winnerName + " " + message + offer + "\n good luck next time!").create().show();
                            displayRoundActivity(winnerName + " " + message + offer);
                        } else {
                            displayRoundActivity(winnerName + " " + message + offer);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
            }
        });
    }

    private void initRoundViews_setData() {
        TextView tvProductName, tvProductPrice, tvSalonId;
        ImageView imProductImage;
        // init views
        tvSalonTime = findViewById(R.id.salon_time);
        tvProductName = findViewById(R.id.salon_round_product_name);
        tvProductPrice = findViewById(R.id.salon_round_product_price);
        imProductImage = findViewById(R.id.salon_round_product_image);
        tvSalonId = findViewById(R.id.salon_number);
        tvRoundActivity = findViewById(R.id.round_notification_text);

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvSalonId.setText(String.valueOf(round.getSalon_id()));

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.placeholder).into(imProductImage);
    }

    private void userOutRound() {
        AlertDialog.Builder alertOut = new AlertDialog.Builder(SalonActivity.this);
        alertOut.setMessage(getString(R.string.leave_salon));
        alertOut.setPositiveButton(getString(R.string.logout_me), outRound);
        alertOut.setNegativeButton(getString(R.string.cancel), null);
        alertOut.setCancelable(false);
        alertOut.create();
        alertOut.show();
    } // check if user keep wanting out

    private DialogInterface.OnClickListener outRound = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "setRoundLeave", new Request(userId, apiToken, salon_id), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {
                    roundStartToEnd.stop(); // stop Time Down
                    stopSalonNotification();
                    getRemainingTimeOfRound();
                    initialConfirmationScreen();
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
                            getRemainingTimeOfRound();
                        }
                    }).show();
                }
            });
        }
    };

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
                        // NotificationDao
                        changeConfirmationState();
                        btn_leave_round.setVisibility(View.VISIBLE);
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

    // start NotificationDao To This Salon
    public void startSalonNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("salon_" + salon_id);
    }

    // stop NotificationDao To This Salon
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
                etAddOffer.setEnabled(true);
                etAddOffer.setText("");
                etAddOffer.setHint(getString(R.string.no_content));
                etAddOffer.setHintTextColor(getResources().getColor(R.color.paleRed));
                return;
            }

            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this,
                    "setUserOffer",
                    new Request(SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                            , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                            , salon_id
                            , userOffer), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            //Save user Offer
                            SharedPrefManager.getInstance(SalonActivity.this).saveUserOffer(String.valueOf(salon_id + "" + userId), userOffer);
                            tvRoundActivity.setText(mainObject.get("message").getAsString());
                            mSocket.emit("addOffer", userName);
                        }

                        @Override
                        public void handleFalseResponse(JsonObject errorObject) {

                        }

                        @Override
                        public void handleEmptyResponse() {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                            etAddOffer.setEnabled(true);
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            addOfferLayout.setVisibility(View.VISIBLE);
                            joinProgress.setVisibility(View.GONE);
                            etAddOffer.setEnabled(true);
                        }
                    });
        } catch (NumberFormatException e) {
            joinProgress.setVisibility(View.GONE);
            addOfferLayout.setVisibility(View.VISIBLE);
        }
        etAddOffer.setEnabled(true);
    }

    private void initJoinConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.join_round_confirmation_layout, null);

        joinIcon = dialogView.findViewById(R.id.join_alert_icon);
        joinHeader = dialogView.findViewById(R.id.join_alert_header);
        joinText = dialogView.findViewById(R.id.join_alert_text);
        joinConfirmationProgress = dialogView.findViewById(R.id.join_alert_progress);
        btnJoinConfirmation = dialogView.findViewById(R.id.btn_join_alert);

        //
        btnJoinConfirmation.setOnClickListener(new View.OnClickListener() {
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
                        tvRoundActivity.setText(getString(R.string.you_are_joined));
                        btn_leave_round.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        dialogBuilder.setView(dialogView);
        joinAlert = dialogBuilder.create();
    }

    private void changeConfirmationState() {
        joinStatus = 2;
        roundStartToEnd.setJoinStatus(joinStatus);
        congratulationScreen();
    }

    private void displayConfirmationLayout() {
        joinStatus = 1;
        roundStartToEnd.setJoinStatus(joinStatus);

        // display confirmation layout
        joinAlert.show();
        btnJoinRound.setVisibility(View.GONE);
    }

    private void initialConfirmationScreen() { // Attention Screen  to Join Round
        joinIcon.setImageDrawable(getResources().getDrawable(R.drawable.q_mark_in_circle));
        joinHeader.setText(getString(R.string.Attention));
        joinHeader.setTextColor(getResources().getColor(R.color.midBlue));
        joinText.setText(getString(R.string.Attention_Details));
        btnJoinConfirmation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnJoinConfirmation.setText(getString(R.string.join_round));
    }

    private void congratulationScreen() { // Congratulation Screen to Join Round
        joinIcon.setImageDrawable(getResources().getDrawable(R.drawable.joinedrounddone));
        joinHeader.setText(getString(R.string.Congratulations_Attention));
        joinHeader.setTextColor(getResources().getColor(R.color.greenBlue));
        joinText.setText(getString(R.string.Congratulations_Attention_Details));
        btnJoinConfirmation.setBackgroundColor(getResources().getColor(R.color.greenBlue));
        btnJoinConfirmation.setText(getString(R.string.start_play));
    }

    public void cancelConfirmation() {
        joinStatus = 0;

        // hide confirmation layout
        btnJoinRound.setVisibility(View.VISIBLE);
        joinAlert.dismiss();
    }

    public void hideConfirmationLayout() {
        // hide confirmation layout
        btnJoinRound.setVisibility(View.GONE);
        addOfferLayout.setVisibility(View.GONE);
        useRoundCard.setVisibility(View.GONE);
        joinAlert.dismiss();
    }

    public void checkOnTime() {

        if (roundRealTimeModel.getRound_status().equals("open")) intiSocket();

        if (roundRealTimeModel.isFree_join_status() && roundRealTimeModel.isUserJoin() || roundRealTimeModel.isPay_join_status() && roundRealTimeModel.isUserJoin()) {
            btn_leave_round.setVisibility(View.VISIBLE);
        } else {
            btn_leave_round.setVisibility(View.GONE);
        }

        if (roundRealTimeModel.isPay_join_status() && !roundRealTimeModel.isUserJoin()) {
            useRoundCard.setVisibility(View.VISIBLE);
        }

        if (roundRealTimeModel.isFirst_round_status() && roundRealTimeModel.isUserJoin() || roundRealTimeModel.isSeconed_round_status() && roundRealTimeModel.isUserJoin()) {
            addOfferLayout.setVisibility(View.VISIBLE);
            //get user Offer
            etAddOffer.setText(String.valueOf(SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(salon_id + "" + userId)));
        } else {
            addOfferLayout.setVisibility(View.GONE);
        }

        if (roundRealTimeModel.isFirst_round_status() || roundRealTimeModel.isSeconed_round_status()) {
            topTenRecycler.setVisibility(View.GONE);
            tvTopTenTab.setVisibility(View.GONE);
        }

        if (roundRealTimeModel.isFirst_rest_status()) {
            // clear user offer
            SharedPrefManager.getInstance(SalonActivity.this).clearUserOffer(salon_id + "" + userId);
            // on first rest display top ten
            tvTopTenTab.setVisibility(View.VISIBLE);
            topTenRecycler.setVisibility(View.VISIBLE);
            selectTopTenTab();
        } else if (roundRealTimeModel.isClose_hall_status() || roundRealTimeModel.getRound_status().equals("close")) {
            chatContainer.setVisibility(View.GONE);
            tvChatTab.setVisibility(View.GONE);
            topTenRecycler.setVisibility(View.VISIBLE);
            tvTopTenTab.setVisibility(View.VISIBLE);
            selectTopTenTab();
            getWinner();
            // diconnect socket
            if (mSocket != null) {
                if (mSocket.connected()) {
                    mSocket.disconnect();
                }
            }
        }


    }

    private void initCardsIcon() {
        RelativeLayout cardsIconContainer = findViewById(R.id.cards_bag_btn_container);
        cardsIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
    }

    private void getUserCardsForSalon() {
        displayLoading();
        int userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "getUserCardsBySalonId", new Request(userId, apiToken, salon_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                userCards.clear();
                userCards.addAll(ParseResponses.parseUserCardsBySalon(mainObject));

                int allUserCardsCount = 0;
                for (Card userCard : userCards) {
                    if (cardList != null) {
                        for (Card salonCard : cardList) {
                            if (userCard.getCard_id() == salonCard.getCard_id()) {
                                salonCard.setCount(userCard.getCount());
                            }
                        }
                    }

                    allUserCardsCount = allUserCardsCount + userCard.getCount();
                }

                calculateGoldenCard();

                if (userCards.size() > 0) {
                    tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                    tvCardsCount.setText(String.valueOf(allUserCardsCount));

                } else {
                    tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_red));
                    tvCardsCount.setText("0");
                }

            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
            }
        });
    }

    public void initBottomSheetActivateCards() {
        getUserCardsForSalon();

        mBottomSheetDialogActivateCard = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_active_cards, null);

        //init bottom sheet views
        if (cardList != null) {
            RecyclerView cardsRecycler = sheetView.findViewById(R.id.salon_cards_bottom_recycler);
            cardsRecycler.setHasFixedSize(true);
            cardsRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));
            cardsRecycler.setAdapter(new BottomCardsAdapter(SalonActivity.this, cardList, userCards, salon_id, round.getRound_id()));
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

    private void addMessageToChat(int user_id, String user_name, String message,String date) {
        chatList.add(new ChatModel(user_id, user_name, message, date));
        chatRecycler.scrollToPosition(chatList.size() - 1);
        ChatAdapter adapter = new ChatAdapter(SalonActivity.this, chatList);
        adapter.notifyDataSetChanged();
        chatRecycler.setAdapter(adapter);
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

        if (mSocket != null) {
            if (mSocket.connected()) {
                mSocket.emit("leave", userName);
                mSocket.disconnect();
            }
        }

        try {
            roundStartToEnd.stop(); // stop Time Down
        } catch (Exception e) {
        }
    }
}