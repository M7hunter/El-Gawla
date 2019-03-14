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
import it_geeks.info.gawla_app.repository.Models.Activity;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.ChatModel;
import it_geeks.info.gawla_app.repository.Models.TopTen;
import it_geeks.info.gawla_app.repository.SocketConnection.SocketConnection;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.ProductSubImage;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.RoundRemainingTime;
import it_geeks.info.gawla_app.repository.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonCardsAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.ProductSubImagesAdapter;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.views.Round.RoundCountDownController;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    // widgets
    private AlertDialog joinAlert;
    private ProgressBar joinProgress, joinConfirmationProgress;
    public VideoView vpProductMainVideo;
    public ImageView imProductMainImage;
    private ImageView btnPlayPause, imgNotification, joinIcon;
    public CardView more, notificationCard, activityContainer, chatContainer, topTenContainer;
    private CardView loadingCard;
    public Button btnJoinRound, btnAddOffer;
    private Button btnJoinConfirmation, btnUseGoldenCard;
    public TextView joinHeader, joinText, tvSalonTime, tvRoundActivity;
    private TextView tvProductDetailsTab, tvSalonActivityTab, tvChatTab, tvTopTenTab, tvChatEmptyHint, tvCardsCount, tvActivityEmptyHint, tvTopTenEmptyHint, btn_leave_round;
    private EditText etAddOffer;
    private View salonMainContainer;
    private LinearLayout addOfferLayout, detailsContainer;
    private RecyclerView chatRecycler, activityRecycler, topTenRecycler;

    // objects
    private Round round;
    private Card goldenCard;
    public ProductSubImage productSubImage = new ProductSubImage();
    public BottomSheetDialog mBottomSheetDialogActivateCard;
    private BottomSheetDialog mBottomSheetDialogProductDetails;
    private Socket mSocket;
    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private String userName, apiToken;
    private int goldenCardCount = 0, stopPosition = 0, screenHeight, screenWidth, joinStatus; // 0 = watcher, 1 = want to join, 2 = joined
    public int userId;
    private boolean socketOnStatus = false;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;

    private RoundCountDownController roundCountDownController;
    private RoundRemainingTime roundRemainingTime;

    // lists
    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();

    private List<ChatModel> chatList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private List<Card> userCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        userName = SharedPrefManager.getInstance(SalonActivity.this).getUser().getName();
        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        roundCountDownController = new RoundCountDownController(SalonActivity.this, new RoundStartToEndModel(upDivsList, downDivsList, drawablesUp, drawablesDown));

        initViews();

        getRoundData(savedInstanceState);

        initJoinConfirmationDialog();

        connectSocket();

        initRoundViews_setData();

        screenDimensions();

        initCardsBagIcon();

        initDivs();

        initChat();

        handleEvents();
    }

    private void initActivityRecycler() {
        if (activityList.size() == 0) {
            tvTopTenEmptyHint.setVisibility(View.VISIBLE);
            activityRecycler.setVisibility(View.GONE);
        } else {
            tvTopTenEmptyHint.setVisibility(View.GONE);
            activityRecycler.setVisibility(View.VISIBLE);
            activityRecycler.setHasFixedSize(true);
            activityRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            activityRecycler.setAdapter(new ActivityAdapter(activityList));
            activityRecycler.smoothScrollToPosition(activityList.size() - 1);
        }
    }

    public void initViews() {
        activityRecycler = findViewById(R.id.salon_activity_recycler);
        topTenRecycler = findViewById(R.id.top_ten_recycler);

        loadingCard = findViewById(R.id.loading_card);
        joinProgress = findViewById(R.id.join_progress);

        more = findViewById(R.id.cv_more);
        notificationCard = findViewById(R.id.round_notification_card);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        topTenContainer = findViewById(R.id.top_ten_container);
        detailsContainer = findViewById(R.id.details_container);
        activityContainer = findViewById(R.id.activity_container);
        chatContainer = findViewById(R.id.chat_container);

        btnAddOffer = findViewById(R.id.add_offer_btn);
        btnJoinRound = findViewById(R.id.btn_join_round);
        btnUseGoldenCard = findViewById(R.id.btn_use_golden_card);
        btn_leave_round = findViewById(R.id.btn_leave_round);

        etAddOffer = findViewById(R.id.add_offer_et);

        tvCardsCount = findViewById(R.id.tv_cards_count);
        tvChatEmptyHint = findViewById(R.id.tv_chat_empty_hint);
        tvTopTenEmptyHint = findViewById(R.id.tv_top_ten_empty_hint);
        tvActivityEmptyHint = findViewById(R.id.tv_activity_empty_hint);
        tvTopTenTab = findViewById(R.id.tv_top_ten);
        tvProductDetailsTab = findViewById(R.id.tv_product_details);
        tvSalonActivityTab = findViewById(R.id.tv_salon_activity);
        tvChatTab = findViewById(R.id.tv_salon_chat);


        imgNotification = findViewById(R.id.Notification);

        apiToken = Common.Instance(SalonActivity.this).removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();
    }

    private void getRoundData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) { // get data from previous page
                round = (Round) extras.getSerializable("round");
            }

        } else { // get data from saved state
            round = (Round) savedInstanceState.getSerializable("round");
        }

        initBottomSheetCardsBag();

        initBottomSheetProductDetails();
    }

    private void calculateGoldenCard() {
        for (int i = 0; i < round.getSalon_cards().size(); i++) {
            if (round.getSalon_cards().get(i).getCard_type().equals("gold")) {
                goldenCard = round.getSalon_cards().get(i);

                goldenCardCount = 0;
                for (Card card : userCards) {
                    if (goldenCard != null)
                        if (card.getCard_type().equals(goldenCard.getCard_type())) {
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
        if (goldenCard != null) {
            btnUseGoldenCard.setBackgroundColor(Color.parseColor(goldenCard.getCard_color()));
        }

        if (goldenCardCount > 0) {
            btnUseGoldenCard.setText(R.string.use_card_to_join);
            //   btnUseGoldenCard.setBackgroundColor(getResources().getColor(R.color.greenBlue));

        } else {
            btnUseGoldenCard.setText(R.string.buy_card_to_join);
            //  btnUseGoldenCard.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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

    public void displayGoldenLayout() {
        btnUseGoldenCard.setVisibility(View.VISIBLE);
    }

    public void hideGoldenLayout() {
        btnUseGoldenCard.setVisibility(View.GONE);
    }

    private void buyGoldenCard() {
        if (goldenCard != null) {
            displayLoading();
            RetrofitClient.getInstance(this).executeConnectionToServer(this, "addCardsToUser", new Request(userId, apiToken, goldenCard.getCard_id()), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {
                    Toast.makeText(SalonActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    initBottomSheetCardsBag();
                }

                @Override
                public void handleFalseResponse(JsonObject errorObject) {

                }

                @Override
                public void handleEmptyResponse() {
                }

                @Override
                public void handleConnectionErrors(String errorMessage) {
                    hideLoading();
                    Toast.makeText(SalonActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void useGoldenCard() {
        if (goldenCard != null) {
            displayLoading();
            hideGoldenLayout();
            RetrofitClient.getInstance(this).executeConnectionToServer(this, "useGoldenCard", new Request(userId, apiToken, goldenCard.getCard_id(), round.getSalon_id(), round.getRound_id()), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {
                    Toast.makeText(SalonActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    roundRemainingTime.setUserJoin(true);
                    goldenCard.setCount(goldenCardCount - 1);
                    initBottomSheetCardsBag();
                    startSalonNotification();
                }

                @Override
                public void handleFalseResponse(JsonObject errorObject) {

                }

                @Override
                public void handleEmptyResponse() {

                }

                @Override
                public void handleConnectionErrors(String errorMessage) {
                    displayGoldenLayout();
                    hideLoading();
                    Toast.makeText(SalonActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getTopTen() {
        displayLoading();
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getTopTen", new Request(userId, apiToken, round.getSalon_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                try {
                    initTopTenRecycler(ParseResponses.parseTopTen(mainObject));
                } catch (Exception e) {
                    Log.e("getTopTen: ", e.getMessage());
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
                Toast.makeText(SalonActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTopTenRecycler(List<TopTen> topTens) {
        if (topTens.size() == 0) {
            tvTopTenEmptyHint.setVisibility(View.VISIBLE);
            tvTopTenEmptyHint.setText(getString(R.string.top_ten_empty));
            topTenRecycler.setVisibility(View.GONE);
        } else {
            tvTopTenEmptyHint.setVisibility(View.GONE);
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
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
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
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));

        // text color
        tvProductDetailsTab.setTextColor(Color.WHITE);
        tvSalonActivityTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvChatTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectActivityTab() {
        detailsContainer.setVisibility(View.GONE);
        activityContainer.setVisibility(View.VISIBLE);
        chatContainer.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.GONE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));

        // text color
        tvProductDetailsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSalonActivityTab.setTextColor(Color.WHITE);
        tvChatTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));

        if (activityList.size() != 0) {
            tvActivityEmptyHint.setVisibility(View.GONE);
            activityRecycler.setVisibility(View.VISIBLE);
        } else {
            tvActivityEmptyHint.setVisibility(View.VISIBLE);
            activityRecycler.setVisibility(View.GONE);
        }
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
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvSalonActivityTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        tvChatTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));

        // text color
        tvProductDetailsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSalonActivityTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvChatTab.setTextColor(Color.WHITE);
        tvTopTenTab.setTextColor(getResources().getColor(R.color.colorPrimary));
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
                try {

                    if (joinStatus == 2 && !roundRemainingTime.getRound_status().equals("close")) {
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
                    } else if (joinStatus != 2) {
                        Toast.makeText(SalonActivity.this, getString(R.string.not_joined), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
                    Log.e("chat_send_message: ", e.getMessage());
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
                            addMessageToChat(user_id, user_name, message, date);
                            tvChatEmptyHint.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            Log.e("socket message", e.getMessage());
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
                } else if (roundRemainingTime.isUserJoin()) {
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
                if (roundRemainingTime.isUserJoin()) {
                    etAddOffer.setEnabled(false);
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
                selectChatTab();
            }
        });

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

    public void connectSocket() {
        mSocket = new SocketConnection().getSocket();
        mSocket.connect();
    }

    private void intiSocket() {
        try {
            JSONObject o = new JSONObject();
            o.put("room", round.getSalon_id());
            o.put("user", userName);
            mSocket.emit("joinRoom", o);
        } catch (JSONException e) {
            Log.e("socket joinRoom: ", e.getMessage());
        }

        mSocket.on("new_member", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundLastActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Log.e("socket newMember: ", e.getMessage());
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
                            displayRoundLastActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Log.e("socket memberAddOffer: ", e.getMessage());
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
                            displayRoundLastActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Log.e("socket memberLeave: ", e.getMessage());
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
                            displayRoundLastActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Log.e("socket winner: ", e.getMessage());
                        }
                    }
                });
            }
        });

        mSocket.emit("allActivity", round.getSalon_id()); // what action triggers this emit ?!
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
                            Log.e("socket activity: ", e.getMessage());
                        }
                    }
                });
            }
        });

        mSocket.on("member_use_card", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            displayRoundLastActivity(main.get("data").toString());
                            activityList.add(new Activity(main.get("data").toString(), main.get("date").toString()));
                            initActivityRecycler();
                        } catch (Exception e) {
                            Log.e("socket memberUseCard: ", e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void displayRoundLastActivity(String notificationMsg) {
        try {
            tvRoundActivity.setText(notificationMsg);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("RoundLastActivity: ", e.getMessage());
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

    public View getSnackBarContainer() {
        if (salonMainContainer == null) {
            salonMainContainer = findViewById(R.id.salon_main_layout);
        }
        return salonMainContainer;
    }

    private void initDivs() {
        /// for time down TODO TimeDown View Init
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

    public void getRemainingTimeOfRound() {
        displayLoading();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "getSalonWithRealTime", new Request(userId, apiToken, round.getSalon_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                if (mainObject.get("isToday").getAsBoolean()) { // today ?
                    initCountDown(ParseResponses.parseRoundRemainingTime(mainObject));

                } else { // !today
                    tvSalonTime.setText(getResources().getString(R.string.round_date) + "\n" + round.getRound_id());
                    tvSalonTime.setTextSize(20);
                }

                round.setRound_id(roundRemainingTime.getLast_round_id());
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
                Snackbar.make(findViewById(R.id.salon_main_layout), errorMessage, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRemainingTimeOfRound();
                    }
                }).show();
            }
        });
    }

    //Round Start
    private void initCountDown(RoundRemainingTime roundRemainingTime) {
        this.roundRemainingTime = roundRemainingTime;
        if (roundRemainingTime.isUserJoin()) {
            joinStatus = 2;
        } else {
            joinStatus = 0;
        }

        roundCountDownController.setJoinStatus(joinStatus);  // User Status From Server
        roundCountDownController.setRoundRemainingTime(roundRemainingTime);// set round remaining time
        try {
            roundCountDownController.updateCountDown();
        } catch (NullPointerException e) {
            Log.e("initCountDown: ", e.getMessage());
        }
    }

    public void checkOnTime() {
        if (roundRemainingTime.getRound_status().equals("open") && !roundRemainingTime.isOpen_hall_status()) {
            if (!socketOnStatus) {
                intiSocket();
                socketOnStatus = true;
            }
            tvChatTab.setVisibility(View.VISIBLE);
        } else {
            tvChatTab.setVisibility(View.GONE);
            chatContainer.setVisibility(View.GONE);
        }

        if (roundRemainingTime.isFree_join_status() && roundRemainingTime.isUserJoin() || roundRemainingTime.isPay_join_status() && roundRemainingTime.isUserJoin()) { // display leave salon btn
            btn_leave_round.setVisibility(View.VISIBLE);
        } else { // hide leave salon btn
            btn_leave_round.setVisibility(View.GONE);
        }

        if (roundRemainingTime.isPay_join_status() && !roundRemainingTime.isUserJoin()) { // display golden card layout
            if (goldenCard != null) {
                displayGoldenLayout();
            } else {
                Log.d("Golden_card:", "id: null");
            }

        } else { // hide golden card layout
            hideGoldenLayout();
        }

        if (roundRemainingTime.isFirst_round_status() && roundRemainingTime.isUserJoin() || roundRemainingTime.isSecond_round_status() && roundRemainingTime.isUserJoin()) { // display add offer layout
            addOfferLayout.setVisibility(View.VISIBLE);
            // get user last Offer
            displayUserOffer();
        } else { // hide add offer layout
            addOfferLayout.setVisibility(View.GONE);
        }

        if (roundRemainingTime.isFirst_round_status() || roundRemainingTime.isSecond_round_status()) { // hide top ten views
            topTenRecycler.setVisibility(View.GONE);
            tvTopTenTab.setVisibility(View.GONE);
        }

        if (roundRemainingTime.isFirst_rest_status()) {
            // clear user offer
            SharedPrefManager.getInstance(SalonActivity.this).clearUserOffer(round.getSalon_id() + "" + userId);
            // display top ten
            tvTopTenTab.setVisibility(View.VISIBLE);
            topTenRecycler.setVisibility(View.VISIBLE);
            selectTopTenTab();
        }

        if (roundRemainingTime.isClose_hall_status() || roundRemainingTime.getRound_status().equals("close")) {
            chatContainer.setVisibility(View.GONE);
            tvChatTab.setVisibility(View.GONE);
            topTenRecycler.setVisibility(View.VISIBLE);
            tvTopTenTab.setVisibility(View.VISIBLE);

            selectTopTenTab();
            getWinner();

            // disconnect socket
            if (mSocket != null) {
                if (mSocket.connected()) {
                    mSocket.disconnect();
                }
            }
        }
    }

    private void displayUserOffer() {
        int offer = SharedPrefManager.getInstance(SalonActivity.this).getUserOffer(round.getSalon_id() + "" + userId);
        if (offer > 0) {
            etAddOffer.setText(String.valueOf(offer));
        } else {
            etAddOffer.setText("");
        }
    }

    private void getWinner() {
        displayLoading();
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getWinner", new Request(userId, apiToken, round.getSalon_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                try {
                    String winnerName = mainObject.get("user_name").getAsString();
                    String message = mainObject.get("message").getAsString();
                    String offer = mainObject.get("offer").getAsString();

                    if (userId == mainObject.get("user_id").getAsInt() && roundRemainingTime.isUserJoin()) { // winner ?
                        Intent i = new Intent(SalonActivity.this, WinnerActivity.class);
                        i.putExtra("winner_name", winnerName);
                        i.putExtra("offer", offer);
                        startActivity(i);

                    } else { // !winner
                        displayRoundLastActivity(winnerName + " " + message + offer);
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
        alertOut.show();
    } // check if user keep wanting x

    private DialogInterface.OnClickListener outRound = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "setRoundLeave", new Request(userId, apiToken, round.getSalon_id()), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {
                    roundCountDownController.stopCountDown();
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
                        , round.getSalon_id()), new HandleResponses() {
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

    // updateCountDown NotificationDao To This Salon
    public void startSalonNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("salon_" + round.getSalon_id());
    }

    // stopCountDown NotificationDao To This Salon
    public void stopSalonNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + round.getSalon_id());
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
                            , round.getSalon_id()
                            , userOffer), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            //Save user Offer
                            SharedPrefManager.getInstance(SalonActivity.this).saveUserOffer(String.valueOf(round.getSalon_id() + "" + userId), userOffer);
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_salon_join_round_confirmation, null);

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
        roundCountDownController.setJoinStatus(joinStatus);
        congratulationScreen();
    }

    private void displayConfirmationLayout() {
        joinStatus = 1;
        roundCountDownController.setJoinStatus(joinStatus);

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
        btnUseGoldenCard.setVisibility(View.GONE);
        joinAlert.dismiss();
    }

    private void initCardsBagIcon() {
        RelativeLayout cardsBagIconContainer = findViewById(R.id.cards_bag_btn_container);
        cardsBagIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
    }

    public void initBottomSheetCardsBag() {
        mBottomSheetDialogActivateCard = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_cards_bag, null);

        //init bottom sheet views
        if (round.getSalon_cards() != null) {
            RecyclerView cardsRecycler = sheetView.findViewById(R.id.salon_cards_bottom_recycler);
            if (cardsRecycler.getLayoutManager() == null) {
                cardsRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));
            }
            cardsRecycler.setHasFixedSize(true);
            getUserCardsForSalonFromServer(cardsRecycler); // <-- refresh user cards list
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

    private void getUserCardsForSalonFromServer(final RecyclerView cardsRecycler) {
        displayLoading();
        int userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token();
        RetrofitClient.getInstance(SalonActivity.this).executeConnectionToServer(SalonActivity.this, "getUserCardsBySalonId", new Request(userId, apiToken, round.getSalon_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                userCards.clear();
                userCards.addAll(ParseResponses.parseUserCardsBySalon(mainObject));

                int allUserCardsCount = 0;
                if (round.getSalon_cards() != null) {
                    for (Card userCard : userCards) {
                        for (Card salonCard : round.getSalon_cards()) {
                            if (userCard.getCard_type().equals(salonCard.getCard_type())) {
                                salonCard.setCount(userCard.getCount());
                                break;
                            }
                        }

                        allUserCardsCount = allUserCardsCount + userCard.getCount();
                    }
                }

                calculateGoldenCard();

                cardsRecycler.setAdapter(new SalonCardsAdapter(SalonActivity.this, round.getSalon_cards(), round.getSalon_id(), round.getRound_id()));

                getRemainingTimeOfRound();

                if (userCards.size() > 0) {
                    tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_green));
                } else {
                    tvCardsCount.setBackground(getResources().getDrawable(R.drawable.bg_circle_red));
                }
                tvCardsCount.setText(String.valueOf(allUserCardsCount));
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

    private void addMessageToChat(int user_id, String user_name, String message, String date) {
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
        if (round.getProduct_images() != null) {
            RecyclerView imagesRecycler = parent.findViewById(R.id.product_details_images_recycler);
            imagesRecycler.setHasFixedSize(true);
            imagesRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false));
            imagesRecycler.setAdapter(new ProductSubImagesAdapter(this, round.getProduct_images()));
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
                view.performClick();

                break;
            default:
                break;
        }

        return true;
    }

    private void handleWithScreenBorders(View view) {
        // if x of the left border || in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2)) {
            view.animate().translationX(0).setDuration(250).start();
        }

        // if x of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2)) {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(250).start();
        }

        // if x of the up border
        if (view.getY() < 0) {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if x of the bottom border
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
        try {
            roundCountDownController.stopCountDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent();
        setResult(RESULT_OK, i);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectionChangeReceiver);

        if (mSocket != null) {
            if (mSocket.connected()) {
                mSocket.emit("leave", userName);
                mSocket.disconnect();
            }
        }

        try {
            roundCountDownController.stopCountDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}