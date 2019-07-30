package it_geeks.info.gawla_app.views.card;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.Adapters.StoreCategoryAdapter;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.util.Constants;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.account.ProfileActivity;

import static it_geeks.info.gawla_app.util.Constants.CATEGORY_NAME;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CATEGORIES;

public class StoreFragment extends Fragment {

    private RecyclerView categoriesRecycler;
    private ProgressBar pbRecycler;
    private ImageView imgNotification;
    private LinearLayout noConnectionLayout, emptyViewLayout;

    private View fragmentView;

    private SnackBuilder snackBuilder;

    //    private List<Card> cardsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_store, container, false);

        initViews();

        handleEvents();

        getDataFromServer();

        return fragmentView;
    }

    private void initViews() {
        pbRecycler = fragmentView.findViewById(R.id.cards_progress);
        categoriesRecycler = fragmentView.findViewById(R.id.cards_recycler);
        categoriesRecycler.setHasFixedSize(true);
        emptyViewLayout = fragmentView.findViewById(R.id.cards_empty_view);
        noConnectionLayout = fragmentView.findViewById(R.id.no_connection);
        //Notification icon
        imgNotification = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), bellIndicator);

        snackBuilder = new SnackBuilder(fragmentView.findViewById(R.id.store_main_layout));
        // load user image
        ImageLoader.getInstance().loadUserImage(MainActivity.mainInstance, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));
    }

    private void handleEvents() {
        // notification
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        fragmentView.findViewById(R.id.iv_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.mainInstance, ProfileActivity.class));
            }
        });
    }

    private void getDataFromServer() {
        if (Common.Instance().isConnected(getContext()))
        {
            if (noConnectionLayout.getVisibility() == View.VISIBLE)
            {
                noConnectionLayout.setVisibility(View.GONE);
            }

            getCategoriesFromServer();
//            getCardsFromServer();
        }
        else
        {
            noConnectionLayout.setVisibility(View.VISIBLE);
            pbRecycler.setVisibility(View.GONE);
        }
    }

    private void getCategoriesFromServer() {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_ALL_CATEGORIES, new Request<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initCategoriesRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initCategoriesRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
                    }
                });
    }

    private void initCategoriesRecycler() {
        if (categoryList.size() > 0)
        {
            categoriesRecycler.setAdapter(new StoreCategoryAdapter(categoryList, new ClickInterface.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Category category = categoryList.get(position);

                    Intent i = new Intent(MainActivity.mainInstance, CategoryCardsActivity.class);
                    i.putExtra(CATEGORY_NAME, category.getCategoryName());
                    i.putExtra(Constants.CAT_ID, category.getCategoryId());
                    startActivity(i);
                }
            }));

            if (pbRecycler.getVisibility() == View.VISIBLE)
                Common.Instance().hideProgress(categoriesRecycler, pbRecycler);
        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            pbRecycler.setVisibility(View.GONE);
        }

    }
}
