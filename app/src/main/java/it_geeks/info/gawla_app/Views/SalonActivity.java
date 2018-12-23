package it_geeks.info.gawla_app.Views;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.Adapters.ProductSubImagesAdapter;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    private List<RelativeLayout> upDivsList = new ArrayList<>();
    private List<RelativeLayout> downDivsList = new ArrayList<>();
    private List<TextView> upNumList = new ArrayList<>();
    private List<TextView> downNumList = new ArrayList<>();


    int joinStatus; // 0 = watcher, 1 = want to join, 2 = joined
    Button btnJoinRound;
    CardView more, notificationCard, confirmationLayout;
    LinearLayout addOfferLayout;
    FrameLayout overlayLayout;

    TextView tvEndTime, tvProductName, tvProductPrice, salonId;
    ImageView imProductImage;

    private Round round;

    private BottomSheetDialog mBottomSheetDialogActivateCard;
    private BottomSheetDialog mBottomSheetDialogSingleCard;
    private BottomSheetDialog mBottomSheetDialogProductDetails;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;
    private int screenWidth;
    private int screenHeight;

    public ImageView imProductMainImage;

    private List<ProductSubImage> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        startTimeDown();

        getRoundData(savedInstanceState);

        initRoundViews_setData();

        screenDimensions();

        initCardsIcon();

        initBottomSheetActivateCards();

        initViews();

        initDivs();

        handleEvents();
    }

    private void initDivs() {

        /// for timedown TODO TimeDown View Init
        for (int i = 0; i < 12; i++) {
            String divUpID = "div_up" + i;
            int resDivIDUp = getResources().getIdentifier(divUpID, "id", getPackageName());
            upDivsList.add((RelativeLayout) findViewById(resDivIDUp));

            String divDownID = "div_down" + i;
            int resDivIDDown = getResources().getIdentifier(divDownID, "id", getPackageName());
            downDivsList.add((RelativeLayout) findViewById(resDivIDDown));

            String numUpID = "num_up" + i;
            int resNumIDUp = getResources().getIdentifier(divDownID, "id", getPackageName());
            upNumList.add((TextView) findViewById(resNumIDUp));

            String numDownID = "num_down" + i;
            int resNumIDDown = getResources().getIdentifier(divDownID, "id", getPackageName());
            downNumList.add((TextView) findViewById(resNumIDDown));

        }
    }

    //timedown
    private void startTimeDown() {
        doSecond();
        doMiute();
        //  doHour();
    }

    private void doSecond() {
        // second
        final CountDownTimer second = new CountDownTimer(60000, 1000) {
            public void onTick(final long millisUntilFinished) {
                int num = (int) millisUntilFinished / 1000;

                final Calendar c = Calendar.getInstance();

                GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(SalonActivity.this,upDivsList,downDivsList,upNumList,downNumList,"second");
                gawlaTimeDownSecond.NumberTick(num);

            }

            public void onFinish() {
            }

        }.start();
    }

    private void doMiute() {
        // Minute
        final CountDownTimer minute = new CountDownTimer(3600000, 60000) {
            public void onTick(final long millisUntilFinished) {
                int num = (int) millisUntilFinished / 60000;

                final Calendar c = Calendar.getInstance();

                GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(SalonActivity.this,upDivsList,downDivsList,upNumList,downNumList,"minute");
                gawlaTimeDownMinute.NumberTick(num);

            }

            public void onFinish() {

            }

        }.start();
    }

    private void doHour() {
        // hour
        new CountDownTimer(86400000, 3600000) {
            public void onTick(final long millisUntilFinished) {
                int num = (int) millisUntilFinished / 3600000;

                final Calendar c = Calendar.getInstance();

                GawlaTimeDown gawlaTimeDownHour = new GawlaTimeDown(SalonActivity.this,upDivsList,downDivsList,upNumList,downNumList,"hour");
                gawlaTimeDownHour.NumberTick(num);

            }

            public void onFinish() {

            }

        }.start();
    }

    private void getRoundData(Bundle savedInstanceState) {
        String product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time;
        int product_id, salon_id;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) { // get data from previous page
                product_id = extras.getInt("product_id");
                salon_id = extras.getInt("salon_id");
                product_name = extras.getString("product_name");
                product_category = extras.getString("category_name");
                product_price = extras.getString("product_commercial_price");
                product_description = extras.getString("product_product_description");
                product_image = extras.getString("product_image");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");

                round = new Round(product_id,
                        salon_id,
                        product_name,
                        product_category,
                        extras.getString("country_name"),
                        product_price,
                        product_description,
                        product_image,
                        round_start_time,
                        round_end_time,
                        extras.getString("first_join_time"),
                        extras.getString("second_join_time"),
                        extras.getString("round_date"),
                        extras.getString("round_time"),
                        extras.getString("rest_time"));
            }

        } else { // get data from saved state
            product_id = savedInstanceState.getInt("product_id");
            salon_id = savedInstanceState.getInt("salon_id");
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_category = (String) savedInstanceState.getSerializable("category_name");
            product_price = (String) savedInstanceState.getSerializable("product_commercial_price");
            product_description = (String) savedInstanceState.getSerializable("product_product_description");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");

            round = new Round(product_id,
                    salon_id,
                    product_name,
                    product_category,
                    (String) savedInstanceState.getSerializable("country_name"),
                    product_price,
                    product_description,
                    product_image,
                    round_start_time,
                    round_end_time,
                    (String) savedInstanceState.getSerializable("first_join_time"),
                    (String) savedInstanceState.getSerializable("second_join_time"),
                    (String) savedInstanceState.getSerializable("round_date"),
                    (String) savedInstanceState.getSerializable("round_time"),
                    (String) savedInstanceState.getSerializable("rest_time"));
        }
    }

    private void initRoundViews_setData() {
        // init views
        tvEndTime = findViewById(R.id.salon_end_time);
        tvProductName = findViewById(R.id.salon_round_product_name);
        tvProductPrice = findViewById(R.id.salon_round_product_price);
        imProductImage = findViewById(R.id.salon_round_product_image);

        // set data
        tvEndTime.setText(round.getRound_end_time());
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.palceholder).into(imProductImage);
    }

    public void initViews() {
        overlayLayout = findViewById(R.id.overlay_layout);
        more = findViewById(R.id.more);
        btnJoinRound = findViewById(R.id.btn_join_round);
        confirmationLayout = findViewById(R.id.join_confirmation_layout);
        notificationCard = findViewById(R.id.round_notification_card);
        addOfferLayout = findViewById(R.id.add_offer_layout);

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
                        changeConfirmationState();
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
                if (mBottomSheetDialogProductDetails.isShowing()) {
                    mBottomSheetDialogProductDetails.dismiss();
                } else { // close sheet
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
    }

    // join events
    private void displayConfirmationLayout() {
        joinStatus = 1;

        // hide background views
        more.setVisibility(View.GONE);
        notificationCard.setVisibility(View.GONE);

        // display confirmation layout
        confirmationLayout.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
    }

    private void changeConfirmationState() {
        joinStatus = 2;

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

    private void hideConfirmationLayout() {
        // display background views
        more.setVisibility(View.VISIBLE);
        notificationCard.setVisibility(View.VISIBLE);

        // hide confirmation layout
        confirmationLayout.setVisibility(View.GONE);
        btnJoinRound.setVisibility(View.GONE);
        overlayLayout.setVisibility(View.GONE);

        // display add offer views
        addOfferLayout.setVisibility(View.VISIBLE);
    }

    private void cancelConfirmation() {
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

        initBottomSheetSingleCard();

        //open single card sheet
        sheetView.findViewById(R.id.btn_activate_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialogSingleCard.show();
            }
        });

        mBottomSheetDialogActivateCard.setContentView(sheetView);

        mBottomSheetDialogActivateCard.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void initBottomSheetSingleCard() {
        mBottomSheetDialogSingleCard = new BottomSheetDialog(this);
        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_single_card, null);

        //init bottom sheet views
        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_single_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogSingleCard.isShowing()) {
                    mBottomSheetDialogSingleCard.dismiss();

                } else {
                    mBottomSheetDialogSingleCard.show();
                }
            }
        });

        mBottomSheetDialogSingleCard.setContentView(sheetView);

        Common.Instance(SalonActivity.this).setBottomSheetHeight(sheetView);

        mBottomSheetDialogSingleCard.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    // product details
    private void initBottomSheetProductDetails() {
        mBottomSheetDialogProductDetails = new BottomSheetDialog(this);
        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);

        //init bottom sheet views
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

        bottomViews_setDetails(sheetView);

        getRoundImages(sheetView);

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

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvProductDescription.setText(round.getProduct_product_description());

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.gawla_logo_blue).into(imProductMainImage);
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

        Intent i = new Intent();
        setResult(RESULT_OK, i);
    }
}
