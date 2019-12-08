package it_geeks.info.elgawla.views.salon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.Adapters.ProductSubImagesAdapter;
import it_geeks.info.elgawla.Adapters.TopTenAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.ProductSubImage;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Models.TopTen;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.AudioPlayer;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_TOP_TEN;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_WINNER;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class ClosedSalonActivity extends BaseActivity {

    private View detailsSheetView, topTenContainer, more;
    public VideoView vpProductMainVideo;
    private ImageView btnPlayPause, ivWinnerImage, ivNotify;
    public ImageView ivProductMainViewer;
    private TextView tvWinnerName, tvWinnerLabel, tvTopTenEmptyHint, tvTopTenTab, tvProductDetailsTab;
    private ProgressBar pbTopTen;
    private RecyclerView topTenRecycler;
    private BottomSheetDialog mBottomSheetDialogProductDetails;

    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private User user;
    private Salon salon;
    private int stopPosition = 0;
    private MutableLiveData<String> winner = new MutableLiveData<>(), winnerImageUrl = new MutableLiveData<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_salon);

        AudioPlayer.getInstance().play(this, R.raw.enter);

        initViews();

        if (getSalonData(savedInstanceState))
        {
            displayWinner();
            bindProductMainViews();
            initBottomSheetProductDetails();
            handleEvents();
        }
    }

    @Override
    public void onBackPressed() {
        AudioPlayer.getInstance().play(this, R.raw.exit);
        setResult(RESULT_OK, new Intent());
        super.onBackPressed();
    }

    private void initViews() {
        tvWinnerName = findViewById(R.id.tv_salon_winner_name);
        tvWinnerLabel = findViewById(R.id.tv_salon_winner_label);
        ivWinnerImage = findViewById(R.id.iv_fs_winner_image);
        tvTopTenEmptyHint = findViewById(R.id.tv_top_ten_empty_hint);
        topTenRecycler = findViewById(R.id.top_ten_recycler);
        tvTopTenTab = findViewById(R.id.tv_top_ten);
        pbTopTen = findViewById(R.id.pb_top_ten);
        tvProductDetailsTab = findViewById(R.id.tv_product_details);
        topTenContainer = findViewById(R.id.top_ten_container);
        more = findViewById(R.id.cv_more);

        detailsSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_product_details, null);
        ivProductMainViewer = detailsSheetView.findViewById(R.id.product_details_main_image);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.closed_salon_main_layout));

        ivNotify = findViewById(R.id.iv_notification_bell);
    }

    private void handleEvents() {
        NotificationBuilder.listenToNotificationStatus(this, findViewById(R.id.bell_indicator));
        ivNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClosedSalonActivity.this, NotificationActivity.class));
            }
        });

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
                            EventsManager.sendViewItemEvent(ClosedSalonActivity.this, "", salon.getProduct_id() + "", "", Double.parseDouble(salon.getProduct_commercial_price()));
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

        findViewById(R.id.fbtn_share_closed_salon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLinkAndShareSalon();
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
                            EventsManager.sendShareEvent(ClosedSalonActivity.this, "salon", String.valueOf(salon.getSalon_id()));
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

    private boolean getSalonData(Bundle savedInstanceState) {
        user = SharedPrefManager.getInstance(this).getUser();

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

        return salon != null;
    }

    private void bindProductMainViews() {
        TextView tvProductName, tvProductPrice, tvSalonId;
        ImageView ivProductImage;
        // init views
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
                imagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

    private void displayWinner() {
        getWinner();
        selectTopTenTab();
        tvWinnerName.setVisibility(View.VISIBLE);
        ivWinnerImage.setVisibility(View.VISIBLE);

        winner.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvWinnerName.setText(s);
            }
        });

        winnerImageUrl.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("no_winner"))
                {
                    tvWinnerLabel.setVisibility(View.INVISIBLE);
                }
                else
                {
                    ImageLoader.getInstance().loadFitImage(s, ivWinnerImage);
                    tvWinnerLabel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getWinner() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).fetchDataFromServer(this,
                REQ_GET_WINNER, new RequestModel<>(REQ_GET_WINNER, user.getUser_id(), user.getApi_token(), salon.getSalon_id(), salon.getRound_id()
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

    private void getTopTen() {
        RetrofitClient.getInstance(this).fetchDataFromServer(this,
                REQ_GET_TOP_TEN, new RequestModel<>(REQ_GET_TOP_TEN, user.getUser_id(), user.getApi_token(), salon.getSalon_id(), salon.getRound_id()
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
                        pbTopTen.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        pbTopTen.setVisibility(View.GONE);
                    }
                });
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

    private void selectTopTenTab() {
        getTopTen();
        topTenRecycler.setVisibility(View.VISIBLE);
        tvTopTenTab.setVisibility(View.VISIBLE);

        more.setVisibility(View.GONE);
        topTenContainer.setVisibility(View.VISIBLE);

        // bgs
        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));

        // text color
        tvProductDetailsTab.setTextColor(Color.BLACK);
        tvTopTenTab.setTextColor(Color.WHITE);
    }

    public void selectDetailsTab() {
        more.setVisibility(View.VISIBLE);
        topTenContainer.setVisibility(View.GONE);

        tvProductDetailsTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_blue));
        tvTopTenTab.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));

        // text color
        tvProductDetailsTab.setTextColor(Color.WHITE);
        tvTopTenTab.setTextColor(Color.BLACK);
    }
}
