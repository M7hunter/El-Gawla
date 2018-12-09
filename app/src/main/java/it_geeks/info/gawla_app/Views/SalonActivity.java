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

    private BottomSheetDialog mBottomSheetDialog;

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

        initBottomSheet();

        if (savedInstanceState != null) {
            joinStatus = savedInstanceState.getInt("Joined");
        }

        initViews();

        joinEvent();
    }

    private void getRoundData(Bundle savedInstanceState) {
        String product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                product_name = extras.getString("product_name");
                product_image = extras.getString("product_image");
                product_category = extras.getString("product_category");
                product_price = extras.getString("product_price");
                product_description = extras.getString("product_description");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");
                joined_members_number = extras.getString("joined_members_number");

                round = new Round(product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number);
            }

        } else {
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            product_category = (String) savedInstanceState.getSerializable("product_category");
            product_price = (String) savedInstanceState.getSerializable("product_price");
            product_description = (String) savedInstanceState.getSerializable("product_description");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");
            joined_members_number = (String) savedInstanceState.getSerializable("joined_members_number");

            round = new Round(product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number);
        }
    }

    private void initRoundViews_setData() {
        // init views
        tvEndTime = findViewById(R.id.hale_end_time);
        tvProductName = findViewById(R.id.hale_product_name);
        tvProductPrice = findViewById(R.id.hale_product_price);
        imProductImage = findViewById(R.id.hale_product_image);

        // set data
        tvEndTime.setText(round.getEnd_time());
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_price());

        Picasso.with(SalonActivity.this).load(round.getProduct_image()).placeholder(R.drawable.gawla_logo_blue).into(imProductImage);
    }

    public void initViews() {
        overlayLayout = findViewById(R.id.overlay_layout);
        more = findViewById(R.id.more);
        btnJoinRound = findViewById(R.id.btnJoinRound);
        confirmationLayout = findViewById(R.id.join_confirmation_layout);
        notificationCard = findViewById(R.id.round_notification_card);
        addOfferLayout = findViewById(R.id.add_offer_layout);
    }

    private void joinEvent() {
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (joinStatus) {
                    case 0:
                        displayConfirmationLayout();
                        joinStatus = 1;
                        break;
                    case 1:
                        changeConfirmationState();
                        joinStatus = 2;
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
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("product_image", round.getProduct_image());
                i.putExtra("product_category", round.getProduct_category());
                i.putExtra("product_price", round.getProduct_price());
                i.putExtra("product_description", round.getProduct_description());
                i.putExtra("round_start_time", round.getStart_time());
                i.putExtra("round_end_time", round.getEnd_time());
                i.putExtra("joined_members_number", round.getJoined_members_number());

                startActivity(i);
            }
        });
    }

    private void displayConfirmationLayout() {
        // hide background views
        more.setVisibility(View.GONE);
        notificationCard.setVisibility(View.GONE);

        // display confirmation layout
        confirmationLayout.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
    }

    private void changeConfirmationState() {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Joined", joinStatus);
    }

    private void initCardsIcon() {
        RelativeLayout cardsIconContainer = findViewById(R.id.cards_icon_container);

        cardsIconContainer.setOnTouchListener(this);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        staringPoint = new PointF(cardsIconContainer.getX(), cardsIconContainer.getY());
    }

    private void initBottomSheet() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_round_update, null);

        //init bottom sheet views
        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialog.isShowing()) {
                    mBottomSheetDialog.dismiss();

                } else {
                    mBottomSheetDialog.show();
                }
            }
        });

        mBottomSheetDialog.setContentView(sheetView);

        mBottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
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
        // if out of the left border or in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2)) {
            view.setX(0);
        }

        // if out of the right border or in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2)) {
            view.setX(screenWidth - view.getWidth());
        }

        // if out of the up border
        if (view.getY() < 0) {
            view.setY(0);
        }

        // if out of the bottom border
        if (view.getY() > (screenHeight - (view.getHeight() / 2))) {
            view.setY(screenHeight - (view.getHeight()));
        }
    }

    private void cardClicked() {
        // open sheet
        if (mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.dismiss();
        } else {
            mBottomSheetDialog.show();
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
