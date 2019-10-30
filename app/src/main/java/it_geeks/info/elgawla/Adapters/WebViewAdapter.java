package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.WebPage;
import it_geeks.info.elgawla.views.menu.WebPageActivity;

public class WebViewAdapter extends RecyclerView.Adapter<WebViewAdapter.ViewHolder> {

    private Context context;
    private List<WebPage> webPageList;

    public WebViewAdapter(Context context, List<WebPage> webPageList) {
        this.context = context;
        this.webPageList = webPageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_web_page_label, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final WebPage webPage = webPageList.get(position);

        // bind
        holder.tvLabel.setText(webPage.getPage_title());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WebPageActivity.class);
                i.putExtra("web_page_url", webPage.getPage_link());
                i.putExtra("web_page_title", webPage.getPage_title());

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return webPageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLabel = itemView.findViewById(R.id.tv_web_view_label);
        }
    }
}
