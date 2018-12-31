package it_geeks.info.gawla_app.Views;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.BottomCardsAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.ProductSubImagesAdapter;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.Views.Round.RoundStartToEnd;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();
    RoundStartToEnd roundStartToEnd;
    public TextView round_notification_text;
    private String product_name, product_image, product_category, category_color, product_price, product_description, round_start_time, round_end_time, first_join_time, second_join_time, round_date, round_time, rest_time;
    int product_id, salon_id;

    int joinStatus; // 0 = watcher, 1 = want to join, 2 = joined
    public Button btnJoinRound, btnAddOffer;
    EditText etAddOffer;
    CardView more, notificationCard, confirmationLayout;
    LinearLayout addOfferLayout;
    RelativeLayout salonContainer;
    FrameLayout overlayLayout;
    ProgressBar salonProgress, joinConfirmationProgress;

    private Round round;

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

    public ProductSubImage productSubImage = new ProductSubImage();

    private List<ProductSubImage> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        initViews();

        getRoundData(savedInstanceState);

        checkIfUserJoinedBefore();

        initRoundViews_setData();

        screenDimensions();

        initCardsIcon();

        initBottomSheetProductDetails();

        initBottomSheetActivateCards();

        initDivs();

        startTimeDown();

        handleEvents();
    }

    private void checkIfUserJoinedBefore() {
        String apiToken = Common.Instance(SalonActivity.this).removeQuotes(SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token());
        int userId = SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id();

        RequestMainBody requestMainBody = new RequestMainBody(
                new Data("getSalonByUserID"),
                new Request(userId, apiToken));

        Call<JsonObject> call = RetrofitClient.getInstance(SalonActivity.this).getAPI().request(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                salonProgress.setVisibility(View.GONE);
                salonContainer.setVisibility(View.VISIBLE);
                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors

                        for (int id : getSalonIdFromResponse(mainObj)) {

                            if (id == salon_id) { // joined before
                                joinStatus = 2;
                                break;

                            } else { // !joined
                                joinStatus = 0;
                            }
                        }

                        roundStartToEnd.setJoinStatus(joinStatus);

                    } else { // errors from server
                        if (handleServerErrors(mainObj).contains("not logged in")) {
                            startActivity(new Intent(SalonActivity.this, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            SharedPrefManager.getInstance(SalonActivity.this).clearUser();
                        }

                        Toast.makeText(SalonActivity.this, handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) { // errors of response body
                    Toast.makeText(SalonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // errors of connection
                salonProgress.setVisibility(View.GONE);
                salonContainer.setVisibility(View.VISIBLE);
                Toast.makeText(SalonActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Integer> getSalonIdFromResponse(JsonObject object) {
        List<Integer> ids = new ArrayList<>();
        JsonArray roundsArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int product_id = roundObj.get("product_id").getAsInt();
            int salon_id = roundObj.get("salon_id").getAsInt();

            ids.add(salon_id);
        }

        return ids;
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
    private void startTimeDown() {

        RoundStartToEndModel roundStartToEndModel = new RoundStartToEndModel(upDivsList, downDivsList, drawablesUp, drawablesDown);
        roundStartToEnd = new RoundStartToEnd(SalonActivity.this, roundStartToEndModel);
        roundStartToEnd.setTime(round_start_time, round_end_time, first_join_time, second_join_time, round_date, round_time, rest_time);
        roundStartToEnd.start();
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


                round = new Round(product_id,
                        salon_id,
                        product_name,
                        product_category,
                        category_color,
                        extras.getString("country_name"),
                        product_price,
                        product_description,
                        product_image,
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

            round = new Round(product_id,
                    salon_id,
                    product_name,
                    product_category,
                    category_color,
                    (String) savedInstanceState.getSerializable("country_name"),
                    product_price,
                    product_description,
                    product_image,
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
        TextView tvEndTime, tvProductName, tvProductPrice, salonId;
        ImageView imProductImage;
        // init views
        tvEndTime = findViewById(R.id.salon_end_time);
        tvProductName = findViewById(R.id.salon_round_product_name);
        tvProductPrice = findViewById(R.id.salon_round_product_price);
        imProductImage = findViewById(R.id.salon_round_product_image);
        salonId = findViewById(R.id.salon_number);
        round_notification_text = findViewById(R.id.round_notification_text);

        // set data
        tvEndTime.setText(round.getRound_end_time());
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        salonId.setText(String.valueOf(round.getSalon_id()));

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.placeholder).into(imProductImage);
    }

    public void initViews() {
        overlayLayout = findViewById(R.id.overlay_layout);
        more = findViewById(R.id.more);
        btnJoinRound = findViewById(R.id.btn_join_round);
        btnAddOffer = findViewById(R.id.add_offer_btn);
        etAddOffer = findViewById(R.id.add_offer_et);
        confirmationLayout = findViewById(R.id.join_confirmation_layout);
        notificationCard = findViewById(R.id.round_notification_card);
        addOfferLayout = findViewById(R.id.add_offer_layout);
        joinConfirmationProgress = findViewById(R.id.join_confirmation_progress);
        salonProgress = findViewById(R.id.salon_progress);
        salonContainer = findViewById(R.id.salon_container);
    }

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
                } else {
                    cancelConfirmation();
                }

            }
        });

        //
        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinStatus == 2) {
                    // switch
                    if (btnAddOffer.getText().equals(getString(R.string.add_offer))) {
                        etAddOffer.setEnabled(false);
                        btnAddOffer.setText(getResources().getString(R.string.edit));
                    } else if (btnAddOffer.getText().equals(getString(R.string.edit))) {
                        etAddOffer.setEnabled(true);
                        btnAddOffer.setText(getResources().getString(R.string.add_offer));
                    }

                    sendOfferToServer();
                }
            }
        });
    }

    private void addUserToSalon() {
        joinConfirmationProgress.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(SalonActivity.this)
                .getAPI().request(new RequestMainBody(new Data("setUserSalon"),
                new Request(SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                        , String.valueOf(Common.Instance(SalonActivity.this).getCurrentTimeInMillis())
                        , ""
                        , salon_id))).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                joinConfirmationProgress.setVisibility(View.GONE);
                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors
                        changeConfirmationState();

                    } else { // errors from server
                        if (handleServerErrors(mainObj).contains("not logged in")) {
                            getApplication().startActivity(new Intent(getApplication(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            SharedPrefManager.getInstance(getApplication()).clearUser();
                        }

                        Toast.makeText(getApplication(), handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) { // errors of response body
                    Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                joinConfirmationProgress.setVisibility(View.GONE);
                Toast.makeText(SalonActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOfferToServer() {
        int userOffer = Integer.parseInt(etAddOffer.getText().toString());

        RetrofitClient.getInstance(SalonActivity.this)
                .getAPI().request(new RequestMainBody(new Data("setUserOffer"),
                new Request(SharedPrefManager.getInstance(SalonActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SalonActivity.this).getUser().getApi_token()
                        , salon_id
                        , String.valueOf(Common.Instance(SalonActivity.this).getCurrentTimeInMillis())
                        , userOffer))).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors

                    } else { // errors from server
                        if (handleServerErrors(mainObj).contains("not logged in")) {
                            getApplication().startActivity(new Intent(getApplication(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            SharedPrefManager.getInstance(getApplication()).clearUser();
                        }

                        Toast.makeText(getApplication(), handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) { // errors of response body
                    Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SalonActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
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
        ImageView icon = findViewById(R.id.join_confirmation_icon);
        TextView header = findViewById(R.id.join_confirmation_header);
        TextView text = findViewById(R.id.join_confirmation_text);

        icon.setImageDrawable(getDrawable(R.drawable.joinedrounddone));
        header.setText(getString(R.string.Congratulations_Attention));
        header.setTextColor(getResources().getColor(R.color.greenBlue));
        text.setText(getString(R.string.Congratulations_Attention_Details));
        btnJoinRound.setBackground(getResources().getDrawable(R.drawable.joined_play_shape));
        btnJoinRound.setText(getString(R.string.start_play));
    }

    public void hideConfirmationLayout() {
        // display background views
        more.setVisibility(View.VISIBLE);
        notificationCard.setVisibility(View.VISIBLE);
        addOfferLayout.setVisibility(View.VISIBLE);

        // hide confirmation layout
        confirmationLayout.setVisibility(View.GONE);
        btnJoinRound.setVisibility(View.GONE);
        overlayLayout.setVisibility(View.GONE);
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
        mBottomSheetDialogActivateCard = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_active_cards, null);

        //init bottom sheet views
        RecyclerView cardsRecycler = sheetView.findViewById(R.id.salon_cards_bottom_recycler);
        cardsRecycler.setHasFixedSize(true);
        cardsRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, 1, false));
        cardsRecycler.setAdapter(new BottomCardsAdapter(SalonActivity.this, GawlaDataBse.getGawlaDatabase(SalonActivity.this).cardDao().getCardsById(round.getSalon_id())));

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
        mBottomSheetDialogProductDetails = new BottomSheetDialog(this);
        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);

        //init bottom sheet views
        getRoundImages(sheetView);
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

    private void bottomViews_setDetails(View parent) {
        TextView tvProductName, tvProductPrice, tvProductDescription;
        // init views
        tvProductName = parent.findViewById(R.id.product_details_name);
        tvProductPrice = parent.findViewById(R.id.product_details_price);
        tvProductDescription = parent.findViewById(R.id.product_details_descriptions);
        imProductMainImage = parent.findViewById(R.id.product_details_main_image);
        vpProductMainVideo = parent.findViewById(R.id.player);
        btnPlayPause = parent.findViewById(R.id.btn_play_pause);

        // set data
        tvProductName.setText(round.getProduct_name());
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
                        btnPlayPause.setImageDrawable(getDrawable(R.drawable.ic_play));

                        handler.removeCallbacks(runnable);
                    }
                } else {
                    vpProductMainVideo.seekTo(stopPosition);
                    vpProductMainVideo.start();

                    btnPlayPause.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    hidePP(handler, runnable);
                }
            }
        });
    }

    private void hidePP(Handler handler, Runnable runnable) {
        handler.postDelayed(runnable, 1500);
    }

    private void getRoundImages(View parent) {
        // get details from database
        imagesList.addAll(GawlaDataBse.getGawlaDatabase(SalonActivity.this).productImageDao().getSubImagesById(round.getProduct_id()));
        if (imagesList.size() != 0) {
            bottomSubImagesRecycler(parent);
        }
    }

    private void bottomSubImagesRecycler(View parent) {
        RecyclerView imagesRecycler = parent.findViewById(R.id.product_details_images_recycler);
        imagesRecycler.setHasFixedSize(true);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ProductSubImagesAdapter productSubImagesAdapter = new ProductSubImagesAdapter(this, imagesList);
        imagesRecycler.setAdapter(productSubImagesAdapter);
    }

    // freedom
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // just clicked
        if (gestureDetector.onTouchEvent(motionEvent)) {
            cardClicked();
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

    private void cardClicked() {
        // open sheet
        if (mBottomSheetDialogActivateCard.isShowing()) {
            mBottomSheetDialogActivateCard.dismiss();
        } else { // close sheet
            mBottomSheetDialogActivateCard.show();
        }
    }

    private void screenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
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
        roundStartToEnd.stop(); // stop Time Down
        Intent i = new Intent();
        setResult(RESULT_OK, i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roundStartToEnd.stop(); // stop Time Down
    }
}
