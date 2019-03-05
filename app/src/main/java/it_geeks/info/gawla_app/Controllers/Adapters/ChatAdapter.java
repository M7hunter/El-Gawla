package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.ChatModel;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    Context context;
    List<ChatModel> chatList = new ArrayList<ChatModel>();

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_chat_room, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ChatModel chatModel = chatList.get(position);
        setHandleData(holder, chatModel);
    }

    private void setHandleData(Holder holder, ChatModel chatModel) {
        holder.chatUsername.setText(chatModel.getUsername());
        holder.chatMessage.setText(chatModel.getMessage());
        holder.messageTime.setText(chatModel.getDate());
        if (chatModel.getId() == SharedPrefManager.getInstance(context).getUser().getUser_id()) {
            holder.chatUsername.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.chat_card_message.setBackground(context.getResources().getDrawable(R.drawable.shape_chat_message_send));
            holder.chatMessage.setTextColor(context.getResources().getColor(R.color.ic_launcher_background));
            holder.messageTime.setTextColor(context.getResources().getColor(R.color.ic_launcher_background));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView chatUsername, chatMessage, messageTime;
        LinearLayout chat_card_message;

        public Holder(@NonNull View itemView) {
            super(itemView);
            chatUsername = itemView.findViewById(R.id.chat_username);
            chatMessage = itemView.findViewById(R.id.chat_message);
            messageTime = itemView.findViewById(R.id.chat_time);
            chat_card_message = itemView.findViewById(R.id.chat_card_message);
        }
    }
}
