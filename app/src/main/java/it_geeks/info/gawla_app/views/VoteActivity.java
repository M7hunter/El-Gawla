package it_geeks.info.gawla_app.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.VoteExpandableAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Vote;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.SnackBuilder;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_VOTES;

public class VoteActivity extends AppCompatActivity {

    private List<Vote> voteList = new ArrayList<>();

    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        initViews();

        getVotesFromServer();
    }

    private void initViews() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void getVotesFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(VoteActivity.this).executeConnectionToServer(
                VoteActivity.this,
                REQ_GET_ALL_VOTES, new Request<>(REQ_GET_ALL_VOTES, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        voteList = ParseResponses.parseVotes(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initList();
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        new SnackBuilder(findViewById(R.id.vote_main_layout)).setSnackText(errorMessage).showSnackbar();
                    }
                });
    }

    private void initList() {
        ExpandableListView voteExpandableListView = findViewById(R.id.vote_expandable_list);
        voteExpandableListView.setAdapter(new VoteExpandableAdapter(this, voteList, findViewById(R.id.vote_main_layout)));
    }
}
