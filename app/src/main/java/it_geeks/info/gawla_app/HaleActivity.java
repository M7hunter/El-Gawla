package it_geeks.info.gawla_app;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HaleActivity extends AppCompatActivity implements View.OnTouchListener {

    int joined;
    Button btnJoineRound, btnJoineRound2, btnJoineRound3;
    RelativeLayout rDivPro1, rDivPro2, rDivPro3, rDivPro4;
    LinearLayout shadowalert, shadowalert2;
    TextView txtReadMore;

    private BottomSheetDialog mBottomSheetDialog;

    private PointF staringPoint = new PointF();
    private PointF pointerPoint = new PointF();
    private GestureDetector gestureDetector;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hale);

        screenDimensions();

        initCardsIcon();

        initBottomSheet();

        if (savedInstanceState != null) {
            joined = savedInstanceState.getInt("Joined");
        }
        setBtnTypeCast();
        setupLoginButton();
        if (joined == 0) {

            rDivPro1.setVisibility(View.VISIBLE);
            rDivPro2.setVisibility(View.VISIBLE);
            rDivPro3.setVisibility(View.GONE);
            btnJoineRound.setVisibility(View.VISIBLE);
            shadowalert.setVisibility(View.GONE);
            shadowalert.setVisibility(View.GONE);
        } else if (joined == 1) {
            rDivPro1.setVisibility(View.VISIBLE);
            rDivPro2.setVisibility(View.VISIBLE);
            shadowalert2.setVisibility(View.GONE);
            rDivPro3.setVisibility(View.GONE);
            btnJoineRound.setVisibility(View.GONE);
            rDivPro4.setVisibility(View.VISIBLE);
        }

    }

    public void setBtnTypeCast() {
        btnJoineRound = findViewById(R.id.btnJoineRound);
        btnJoineRound2 = findViewById(R.id.btnJoineRound2);
        btnJoineRound3 = findViewById(R.id.btnJoineRound3);
        rDivPro1 = findViewById(R.id.divPro1);
        rDivPro2 = findViewById(R.id.divPro2);
        rDivPro3 = findViewById(R.id.divPro3);
        rDivPro4 = findViewById(R.id.divPro4);
        shadowalert = findViewById(R.id.shadowalert);
        shadowalert2 = findViewById(R.id.shadowalert2);
        txtReadMore = findViewById(R.id.txtReadMore);

    }

    private void setupLoginButton() {
        btnJoineRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rDivPro1.setVisibility(View.GONE);
                rDivPro2.setVisibility(View.GONE);
                rDivPro3.setVisibility(View.INVISIBLE);
                btnJoineRound.setVisibility(View.INVISIBLE);
                shadowalert.setVisibility(View.VISIBLE);
            }
        });

        shadowalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rDivPro1.setVisibility(View.VISIBLE);
                rDivPro2.setVisibility(View.VISIBLE);
                rDivPro3.setVisibility(View.GONE);
                btnJoineRound.setVisibility(View.VISIBLE);
                shadowalert.setVisibility(View.GONE);
            }
        });

        btnJoineRound2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadowalert2.setVisibility(View.VISIBLE);
                shadowalert.setVisibility(View.GONE);
                joined = 1;
            }
        });
        btnJoineRound3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rDivPro1.setVisibility(View.VISIBLE);
                rDivPro2.setVisibility(View.VISIBLE);
                shadowalert2.setVisibility(View.GONE);
                rDivPro3.setVisibility(View.GONE);
                btnJoineRound.setVisibility(View.GONE);
                rDivPro4.setVisibility(View.VISIBLE);
            }
        });
        txtReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HaleActivity.this, ProductDetailsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (joined == 0) {

            rDivPro1.setVisibility(View.VISIBLE);
            rDivPro2.setVisibility(View.VISIBLE);
            rDivPro3.setVisibility(View.GONE);
            btnJoineRound.setVisibility(View.VISIBLE);
            shadowalert.setVisibility(View.GONE);
            shadowalert.setVisibility(View.GONE);
            super.onBackPressed();

        } else if (joined == 1) {
            rDivPro1.setVisibility(View.VISIBLE);
            rDivPro2.setVisibility(View.VISIBLE);
            shadowalert2.setVisibility(View.GONE);
            rDivPro3.setVisibility(View.GONE);
            btnJoineRound.setVisibility(View.GONE);
            rDivPro4.setVisibility(View.VISIBLE);
            super.onBackPressed();

        }
    }
///////////////////////////// mo mmmmmmmmmm
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Joined", joined);
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
