package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.repository.Models.WinnerNews;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.WinnersActivity;

public class WinnersNewsAdapter extends RecyclerView.Adapter<WinnersNewsAdapter.ViewHolder> {

    private Context context;
    private List<WinnerNews> winnerNewsList;

    public WinnersNewsAdapter(Context context, List<WinnerNews> winnerNewsList) {
        this.context = context;
        this.winnerNewsList = winnerNewsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_winners, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final WinnerNews news = winnerNewsList.get(position);

        if (news.getBlog_imagesArr().size() > 0)
            ImageLoader.getInstance().loadImage(news.getBlog_imagesArr().get(0), viewHolder.imgNewsImage);

        viewHolder.tvNewsTitle.setText(news.getBog_title());
        viewHolder.tvNewsBody.setText(news.getBlog_description());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WinnersActivity.class);
                i.putExtra("news_title", news.getBog_title());
                i.putExtra("news_body", news.getBlog_description());
                if (news.getBlog_imagesArr().size() > 0)
                {
                    i.putExtra("news_image", news.getBlog_imagesArr().get(0));
                }

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return winnerNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNewsTitle, tvNewsBody;
        ImageView imgNewsImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgNewsImage = itemView.findViewById(R.id.news_image);
            tvNewsTitle = itemView.findViewById(R.id.news_title);
            tvNewsBody = itemView.findViewById(R.id.news_body);
        }
    }
}
