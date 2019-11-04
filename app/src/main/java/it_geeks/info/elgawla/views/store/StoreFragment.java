package it_geeks.info.elgawla.views.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.StoreCategoryAdapter;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.Storage.CategoryDao;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.Constants;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.util.Constants.CATEGORY_NAME;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_CATEGORIES;

public class StoreFragment extends Fragment {

    private Context context;
    private RecyclerView categoriesRecycler;
    private ProgressBar pbRecycler;
    private ImageView imgNotification;
    private LinearLayout emptyViewLayout;

    private SnackBuilder snackBuilder;
    private CategoryDao categoryDao;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryDao = GawlaDataBse.getInstance(context).categoryDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        initViews(fragmentView);

        initCategoriesRecycler();

        handleEvents(fragmentView);

        updateCategoriesFromServer();
    }

    private void initViews(View fragmentView) {
        pbRecycler = fragmentView.findViewById(R.id.cards_progress);
        categoriesRecycler = fragmentView.findViewById(R.id.cards_recycler);
        categoriesRecycler.setHasFixedSize(true);
        emptyViewLayout = fragmentView.findViewById(R.id.cards_empty_view);
        //Notification icon
        imgNotification = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(getContext(), bellIndicator);

        snackBuilder = new SnackBuilder(fragmentView.findViewById(R.id.store_main_layout));
        // load user image
        ImageLoader.getInstance().loadUserImage(context, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));
    }

    private void handleEvents(View fragmentView) {
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
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });
    }

    private void updateCategoriesFromServer() {
        List<Category> checkList = categoryDao.getCategories().getValue();
        if (checkList != null && checkList.size() > 0)
        {
            pbRecycler.setVisibility(View.VISIBLE);
            emptyViewLayout.setVisibility(View.GONE);
        }

        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(context,
                REQ_GET_ALL_CATEGORIES, new RequestModel<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryDao.insertCategories(ParseResponses.parseCategories(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initCategoriesRecycler() {
        categoryDao.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(final List<Category> categories) {
                if (categories.size() > 0)
                {
                    emptyViewLayout.setVisibility(View.GONE);
                    categoriesRecycler.setAdapter(new StoreCategoryAdapter(categories, new ClickInterface.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Category category = categories.get(position);

                            Intent i = new Intent(context, CategoryCardsActivity.class);
                            i.putExtra(CATEGORY_NAME, category.getCategoryName());
                            i.putExtra(Constants.CAT_ID, category.getCategoryId());
                            startActivity(i);
                        }
                    }));

                    if (pbRecycler.getVisibility() == View.VISIBLE)
                        Common.Instance().hideProgress(categoriesRecycler, pbRecycler);
                }
                else
                { // empty
                    emptyViewLayout.setVisibility(View.VISIBLE);
                    pbRecycler.setVisibility(View.GONE);
                }
            }
        });
    }
}
