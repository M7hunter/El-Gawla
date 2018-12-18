package it_geeks.info.gawla_app.Views;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.squareup.picasso.Picasso;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;

public class SalonActivity extends AppCompatActivity implements View.OnTouchListener {

    int joinStatus; // 0 = watcher, 1 = want to join, 2 = joined
    Button btnJoinRound;
    CardView more, notificationCard, confirmationLayout;
    LinearLayout addOfferLayout;
    FrameLayout overlayLayout;

    TextView tvEndTime, tvProductName, tvProductPrice;
    ImageView imProductImage;

    private Round round;

    private BottomSheetDialog mBottomSheetDialogActivateCard;
    private BottomSheetDialog mBottomSheetDialogSingleCard;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        getRoundData(savedInstanceState);

        initRoundViews_setData();

        screenDimensions();

        initCardsIcon();

        initBottomSheetActivateCards();

        initViews();

        handleEvents();
    }

    private void getRoundData(Bundle savedInstanceState) {
        String product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) { // get data from previous page
                product_name = extras.getString("product_name");
                product_category = extras.getString("category_name");
                product_price = extras.getString("product_commercial_price");
                product_description = extras.getString("product_product_description");
                product_image = extras.getString("product_image");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");

                round = new Round(product_name,
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
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_category = (String) savedInstanceState.getSerializable("category_name");
            product_price = (String) savedInstanceState.getSerializable("product_commercial_price");
            product_description = (String) savedInstanceState.getSerializable("product_product_description");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");

            round = new Round(product_name,
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

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.gawla_logo_blue).into(imProductImage);
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

        // open product description page
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SalonActivity.this, ProductDetailsActivity.class);
                // send round's data to more page
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("category_name", round.getCategory_name());
                i.putExtra("country_name", round.getCountry_name());
                i.putExtra("product_commercial_price", round.getProduct_commercial_price());
                i.putExtra("product_product_description", round.getProduct_product_description());
                i.putExtra("product_image", round.getProduct_image());
                i.putExtra("round_start_time", round.getRound_start_time());
                i.putExtra("round_end_time", round.getRound_end_time());
                i.putExtra("first_join_time", round.getFirst_join_time());
                i.putExtra("second_join_time", round.getSecond_join_time());
                i.putExtra("round_date", round.getRound_date());
                i.putExtra("round_time", round.getRound_time());
                i.putExtra("rest_time", round.getRest_time());

                startActivity(i);
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
}
