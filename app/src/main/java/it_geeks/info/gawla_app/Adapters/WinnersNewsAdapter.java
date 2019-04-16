package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.ImageLoader;
import it_geeks.info.gawla_app.repository.Models.WinnerNews;
import it_geeks.info.gawla_app.R;

public class WinnersNewsAdapter extends RecyclerView.Adapter<WinnersNewsAdapter.ViewHolder> {

    private Context context;
    private List<WinnerNews> winnerNewsList;

    public WinnersNewsAdapter(Context context, List<WinnerNews> roundsList) {
        this.context = context;
        this.winnerNewsList = roundsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        WinnerNews news = winnerNewsList.get(position);

        if (news.getBlog_imagesArr().size() > 0)
            ImageLoader.getInstance().loadImage(news.getBlog_imagesArr().get(0), viewHolder.imgNewsImage);
        viewHolder.tvNewsTitle.setText(news.getBog_title());
        viewHolder.tvNewsBody.setText(news.getBlog_description());
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
