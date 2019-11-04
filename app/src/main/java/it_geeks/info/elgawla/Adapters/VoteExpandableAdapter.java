package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Vote;
import it_geeks.info.elgawla.repository.Models.VoteChild;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.menu.VoteActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_SET_VOTE;

public class VoteExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Vote> votes;
    private SnackBuilder snackBuilder;

    public VoteExpandableAdapter(Context context, List<Vote> votes, View parentView) {
        this.context = context;
        this.votes = votes;
        snackBuilder = new SnackBuilder(parentView);
    }

    @Override
    public int getGroupCount() {
        return votes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return votes.get(groupPosition).getVoteChildList() != null ? votes.get(groupPosition).getVoteChildList().size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return votes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return votes.get(groupPosition).getVoteChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote_cat_header, parent, false);
        }

        TextView tvHeaderTitle = convertView.findViewById(R.id.tv_vote_header_title);
        TextView tvHeaderCount = convertView.findViewById(R.id.tv_votes_header_count);
//        ImageView ivIcon = convertView.findViewById(R.id.iv_vote_header_icon);
        final ImageView ivHeaderVote = convertView.findViewById(R.id.iv_vote_header);
//        View line = convertView.findViewById(R.id.lv_vote);

        final Vote vote = votes.get(groupPosition);

//        line.setBackgroundColor(Color.parseColor(vote.getColor()));
//        ImageLoader.getInstance().loadIcon(vote.getIcon(), ivIcon);
//        tvHeaderTitle.setTextColor(Color.parseColor(vote.getColor()));
        tvHeaderTitle.setText(vote.getTitle());
        tvHeaderCount.setText(context.getString(R.string.votes, vote.getCount()));

        if (vote.isVoted()) {
            ivHeaderVote.setImageDrawable(context.getDrawable(R.drawable.voted));
        } else {
            ivHeaderVote.setImageDrawable(context.getDrawable(R.drawable.unvoted));
        }

        ivHeaderVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vote.isVoted()) {
                    sendVoteToServer(ivHeaderVote, vote.getId(), false);
                } else {
                    sendVoteToServer(ivHeaderVote, vote.getId(), true);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote_cat_child, parent, false);
        }

        TextView tvChildTitle = convertView.findViewById(R.id.tv_vote_child_title);
        TextView tvChildCount = convertView.findViewById(R.id.tv_votes_child_count);
        final ImageView ivChildVote = convertView.findViewById(R.id.iv_vote_child);

        final VoteChild voteChild = votes.get(groupPosition).getVoteChildList().get(childPosition);

        tvChildTitle.setText(voteChild.getTitle());
        tvChildCount.setText(voteChild.getCount());

        if (voteChild.isVoted()) {
            ivChildVote.setImageDrawable(context.getDrawable(R.drawable.voted));
        } else {
            ivChildVote.setImageDrawable(context.getDrawable(R.drawable.unvoted));
        }

        ivChildVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voteChild.isVoted()) {
                    sendVoteToServer(ivChildVote, voteChild.getId(), false);
                } else {
                    sendVoteToServer(ivChildVote, voteChild.getId(), true);
                }
            }
        });

        return convertView;
    }

    private void sendVoteToServer(final ImageView ivVote, int voteId, boolean voted) {
        ((VoteActivity) context).dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(context).executeConnectionToServer(
                context,
                REQ_SET_VOTE, new RequestModel<>(REQ_SET_VOTE, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token()
                        , voteId, voted, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                        boolean voted = mainObject.get("voted").getAsBoolean();

                        if (voted) {
                            ivVote.setImageDrawable(context.getDrawable(R.drawable.voted));
                        } else {
                            ivVote.setImageDrawable(context.getDrawable(R.drawable.unvoted));
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((VoteActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((VoteActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
