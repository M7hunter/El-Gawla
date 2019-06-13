package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.ChatModel;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    private Context context;
    private List<ChatModel> chatList;

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ChatModel chatModel = chatList.get(position);

        holder.chatUsername.setText(chatModel.getUsername());
        holder.chatMessage.setText(chatModel.getMessage());
        holder.messageTime.setText(chatModel.getDate());

        if (chatModel.getId() == SharedPrefManager.getInstance(context).getUser().getUser_id())
        { // this user
            holder.chatUsername.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.chatMessage.setTextColor(context.getResources().getColor(R.color.ic_launcher_background));
            holder.messageTime.setTextColor(Color.GRAY);
            holder.chatMessage.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_bottom_corners_primary));
        }
        else
        { // others
            holder.chatUsername.setTextColor(context.getResources().getColor(R.color.browser_actions_text_color));
            holder.chatMessage.setTextColor(context.getResources().getColor(R.color.browser_actions_text_color));
            holder.messageTime.setTextColor(Color.GRAY);
            holder.chatMessage.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_bottom_corners_pale_grey));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView chatUsername, chatMessage, messageTime;

        Holder(@NonNull View itemView) {
            super(itemView);

            chatUsername = itemView.findViewById(R.id.chat_username);
            chatMessage = itemView.findViewById(R.id.chat_message);
            messageTime = itemView.findViewById(R.id.chat_time);
        }
    }
}
