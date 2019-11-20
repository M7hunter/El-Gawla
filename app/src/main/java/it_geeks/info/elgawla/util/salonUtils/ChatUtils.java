package it_geeks.info.elgawla.util.salonUtils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.ChatAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.ChatModel;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.salon.SalonActivity;

public class ChatUtils {

    private Context mContext;
    private View salonMainView;
    TextSwitcher tvChatTypingState;
    private TextView tvChatEmptyHint, tvNewMessagesCounter;
    private EditText etChatMessage;
    private ImageButton btnSendMsg;
    private RecyclerView chatRecycler;

    private List<ChatModel> chatList = new ArrayList<>();
    private Salon salon;

    public boolean sendTypingState = true;
    private int messCounter = 0;

    public ChatUtils(Context context, View salonMainLayout) {
        mContext = context;
        salonMainView = salonMainLayout;
        salon = ((SalonActivity) mContext).getSalon();

        initViews();

        initChat();
    }

    private void initViews() {
        tvNewMessagesCounter = salonMainView.findViewById(R.id.tv_new_chat_message_counter);
        tvChatTypingState = salonMainView.findViewById(R.id.tv_chat_typing_state);
        tvChatTypingState = salonMainView.findViewById(R.id.tv_chat_typing_state);
        tvChatEmptyHint = salonMainView.findViewById(R.id.tv_chat_empty_hint);
        etChatMessage = salonMainView.findViewById(R.id.et_chat_message);
        btnSendMsg = salonMainView.findViewById(R.id.btn_send_chat_message);

        chatRecycler = salonMainView.findViewById(R.id.chat_list);
    }

    public void selectChatTab() {
        ((SalonActivity) mContext).more.setVisibility(View.GONE);
        ((SalonActivity) mContext).lastActivity.setVisibility(View.GONE);
        ((SalonActivity) mContext).activityContainer.setVisibility(View.GONE);
        ((SalonActivity) mContext).chatContainer.setVisibility(View.VISIBLE);
        ((SalonActivity) mContext).topTenContainer.setVisibility(View.GONE);
        tvNewMessagesCounter.setVisibility(View.INVISIBLE);
        messCounter = 0;

        if (chatList.size() > 0)
        {
            tvChatEmptyHint.setVisibility(View.GONE);
        }
        else
        {
            tvChatEmptyHint.setVisibility(View.VISIBLE);
        }

        // bgs
        ((SalonActivity) mContext).tvProductDetailsTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        ((SalonActivity) mContext).tvSalonActivityTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));
        ((SalonActivity) mContext).tvChatTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_blue));
        ((SalonActivity) mContext).tvTopTenTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_grey));

        // text color
        ((SalonActivity) mContext).tvProductDetailsTab.setTextColor(Color.BLACK);
        ((SalonActivity) mContext).tvSalonActivityTab.setTextColor(Color.BLACK);
        ((SalonActivity) mContext).tvChatTab.setTextColor(Color.WHITE);
        ((SalonActivity) mContext).tvTopTenTab.setTextColor(Color.BLACK);
    }

    private void initChat() {
        chatRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setAdapter(new ChatAdapter(mContext, chatList));

        initTypingSwitcher();

        final String userName = SharedPrefManager.getInstance(mContext).getUser().getName();
        final int userId = SharedPrefManager.getInstance(mContext).getUser().getUser_id();

        etChatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", "s:: " + s);
                try
                {
                    if (salon != null)
                    {
                        JSONObject obj = new JSONObject();
                        obj.put("salon_id", salon.getSalon_id());
                        obj.put("user", userName);
                        obj.put("user_id", userId);
                        obj.put("lang", SharedPrefManager.getInstance(mContext).getSavedLang());

                        if (s.length() > 0)
                        {
                            if (sendTypingState)
                            {
                                ((SalonActivity) mContext).getSocketUtils().emitData("Typing", obj);
                                sendTypingState = false;
                            }
                        }
                        else
                        {
                            ((SalonActivity) mContext).getSocketUtils().emitData("leaveTyping", obj);
                            sendTypingState = true;
                        }
                    }
                }
                catch (JSONException e)
                {
                    sendTypingState = true;
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if (((SalonActivity) mContext).getRoundRemainingTime().isUserJoin() && !((SalonActivity) mContext).getRoundRemainingTime().getRound_status().equals("close"))
                    {
                        if (etChatMessage.getText().toString().trim().isEmpty())
                        {
                            etChatMessage.setError(mContext.getString(R.string.empty_hint));
                        }
                        else
                        {
                            JSONObject obj = new JSONObject();
                            final String message = etChatMessage.getText().toString();
                            try
                            {
                                obj.put("user_id", userId);
                                obj.put("user_name", userName);
                                obj.put("message", message);
                                obj.put("salon_id", salon.getSalon_id());
                                obj.put("lang", SharedPrefManager.getInstance(mContext).getSavedLang());
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }

                            ((SalonActivity) mContext).getSocketUtils().emitData("newMessage", obj);
                            etChatMessage.setText("");
                        }
                    }
                    else if (((SalonActivity) mContext).getRoundRemainingTime().getRound_status().equals("close"))
                    {
                        Toast.makeText(mContext, mContext.getString(R.string.closed), Toast.LENGTH_SHORT).show();
                    }
                    else if (!((SalonActivity) mContext).getRoundRemainingTime().isUserJoin())
                    {
                        Toast.makeText(mContext, mContext.getString(R.string.not_joined), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (NullPointerException e)
                {
                    Log.e("chat_send_message: ", e.getMessage());
                    Crashlytics.logException(e);
                }
            }
        });
    }

    private void initTypingSwitcher() {
        tvChatTypingState.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(mContext);
                tv.setTextSize(8);
                tv.setGravity(Gravity.TOP);
                tv.setTextColor(mContext.getResources().getColor(R.color.blueGrey));
                return tv;
            }
        });

        tvChatTypingState.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_bottom_up));
        tvChatTypingState.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_bottom_down));
    }

    void addMessageToChat(int user_id, String user_name, String message, String date) {
        chatList.add(0, new ChatModel(user_id, user_name, message, date));
        if (chatRecycler.getAdapter() != null)
        {
            chatRecycler.getAdapter().notifyItemInserted(0);
        }
        chatRecycler.scrollToPosition(0);
        updateCounter();

        // decrease list size
        if (chatList.size() > 500)
        {
            chatList.removeAll(chatList.subList(499, chatList.size() - 1));
        }
    }

    private void updateCounter() {
        if (((SalonActivity) mContext).chatContainer.getVisibility() != View.VISIBLE)
        {
            messCounter = messCounter + 1;
            if (messCounter <= 10)
            {
                tvNewMessagesCounter.setText(String.valueOf(messCounter));
            }
            else
            {
                tvNewMessagesCounter.setText(10 + "+");
            }
            if (tvNewMessagesCounter.getVisibility() != View.VISIBLE)
                tvNewMessagesCounter.setVisibility(View.VISIBLE);
        }
    }

    public void enableChat() {
        tvChatEmptyHint.setText(mContext.getString(R.string.chat_empty_hint));
        etChatMessage.setVisibility(View.VISIBLE);
        btnSendMsg.setVisibility(View.VISIBLE);
    }

    public void disableChat() {
        tvChatEmptyHint.setText(mContext.getString(R.string.chat_is_closed));
        etChatMessage.setVisibility(View.GONE);
        btnSendMsg.setVisibility(View.GONE);
    }
}
