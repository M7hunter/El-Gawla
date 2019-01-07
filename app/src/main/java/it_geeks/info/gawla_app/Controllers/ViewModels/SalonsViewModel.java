package it_geeks.info.gawla_app.Controllers.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RecentSalonsCallback;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;

public class SalonsViewModel extends AndroidViewModel {

    private GawlaDataBse gawlaDataBse;

    private LiveData<PagedList<Round>> roundsList;

    public SalonsViewModel(@NonNull Application application) {
        super(application);

        gawlaDataBse = GawlaDataBse.getGawlaDatabase(getApplication());
    }

    public void init() {
        DataSource.Factory<Integer, Round> factory = gawlaDataBse.roundDao().getRoundsPaged();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(2)
                .setInitialLoadSizeHint(5)
                .setPrefetchDistance(2)
                .setEnablePlaceholders(true)
                .build();

        roundsList = new LivePagedListBuilder<>(factory, config)
                .setBoundaryCallback(new RecentSalonsCallback(getApplication(), gawlaDataBse, SharedPrefManager.getInstance(getApplication())))
                .build();
    }

    public LiveData<PagedList<Round>> getRoundsList() {
        return roundsList;
    }
}
