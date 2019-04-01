package it_geeks.info.gawla_app.views.salonUtils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import it_geeks.info.gawla_app.Adapters.ChatAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.ChatModel;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.SalonActivity;

public class ChatUtils {

    private Context mContext;
    private View salonMainView;
    TextSwitcher tvChatTypingState;
    private TextView tvChatEmptyHint;
    private EditText etChatMessage;
    private Button btnSendMsg;
    private RecyclerView chatRecycler;

    private List<ChatModel> chatList = new ArrayList<>();
    private Round round;

    public boolean sendTypingState = false;

    public ChatUtils(Context context, View salonMainLayout) {
        mContext = context;
        salonMainView = salonMainLayout;
        round = ((SalonActivity) mContext).getRound();

        initViews();

        initChat();
    }

    private void initViews() {
        tvChatTypingState = salonMainView.findViewById(R.id.tv_chat_typing_state);
        tvChatTypingState = salonMainView.findViewById(R.id.tv_chat_typing_state);
        tvChatEmptyHint = salonMainView.findViewById(R.id.tv_chat_empty_hint);
        etChatMessage = salonMainView.findViewById(R.id.et_chat_message);
        btnSendMsg = salonMainView.findViewById(R.id.btn_send_chat_message);

        chatRecycler = salonMainView.findViewById(R.id.chat_list);
    }

    public void selectChatTab() {
        ((SalonActivity) mContext).detailsContainer.setVisibility(View.GONE);
        ((SalonActivity) mContext).activityContainer.setVisibility(View.GONE);
        ((SalonActivity) mContext).chatContainer.setVisibility(View.VISIBLE);
        ((SalonActivity) mContext).topTenContainer.setVisibility(View.GONE);

        if (chatList.size() > 0) {
            tvChatEmptyHint.setVisibility(View.GONE);
        } else {
            tvChatEmptyHint.setVisibility(View.VISIBLE);
        }

        // bgs
        ((SalonActivity) mContext).tvProductDetailsTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        ((SalonActivity) mContext).tvSalonActivityTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));
        ((SalonActivity) mContext).tvChatTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_blue));
        ((SalonActivity) mContext).tvTopTenTab.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rectangle_white_border_midblue));

        // text color
        ((SalonActivity) mContext).tvProductDetailsTab.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        ((SalonActivity) mContext).tvSalonActivityTab.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        ((SalonActivity) mContext).tvChatTab.setTextColor(Color.WHITE);
        ((SalonActivity) mContext).tvTopTenTab.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
    }

    private void initChat() {
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        chatRecycler.setAdapter(new ChatAdapter(mContext, chatList));

        initTypingSwitcher();

        etChatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (round != null) {
                        if (count > 0) {
                            if (sendTypingState) {
                                JSONObject obj = new JSONObject();
                                obj.put("user", SharedPrefManager.getInstance(mContext).getUser().getName());
                                obj.put("salon_id", round.getSalon_id());

                                ((SalonActivity) mContext).getSocketUtils().emitData("Typing", obj);
                                sendTypingState = false;
                            }
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("user", SharedPrefManager.getInstance(mContext).getUser().getName());
                            obj.put("salon_id", round.getSalon_id());

                            ((SalonActivity) mContext).getSocketUtils().emitData("leaveTyping", obj);
                            sendTypingState = true;
                        }
                    }
                } catch (
                        JSONException e) {
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
                try {
                    if (((SalonActivity) mContext).getRoundRemainingTime().isUserJoin() && !((SalonActivity) mContext).getRoundRemainingTime().getRound_state().equals("close")) {
                        if (etChatMessage.getText().toString().trim().isEmpty()) {
                            etChatMessage.setError(mContext.getString(R.string.empty_hint));
                        } else {
                            JSONObject obj = new JSONObject();
                            final String message = etChatMessage.getText().toString();
                            try {
                                obj.put("user_id", SharedPrefManager.getInstance(mContext).getUser().getUser_id());
                                obj.put("user_name", SharedPrefManager.getInstance(mContext).getUser().getName());
                                obj.put("message", message);
                                obj.put("salon_id", round.getSalon_id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }

                            ((SalonActivity) mContext).getSocketUtils().emitData("newMessage", obj);
                            etChatMessage.setText("");
                        }
                    } else if (((SalonActivity) mContext).getRoundRemainingTime().getRound_state().equals("close")) {
                        Toast.makeText(mContext, mContext.getString(R.string.closed), Toast.LENGTH_SHORT).show();

                    } else if (!((SalonActivity) mContext).getRoundRemainingTime().isUserJoin()) {
                        Toast.makeText(mContext, mContext.getString(R.string.not_joined), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
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

        tvChatTypingState.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_top));
        tvChatTypingState.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_bottom));
    }

    void addMessageToChat(int user_id, String user_name, String message, String date) {
        chatList.add(new ChatModel(user_id, user_name, message, date));
        chatRecycler.setAdapter(new ChatAdapter(mContext, chatList));
        chatRecycler.scrollToPosition(chatList.size() - 1);
    }

    public void enableChat() {
        tvChatEmptyHint.setText(mContext.getString(R.string.chat_empty_hint));
        etChatMessage.setEnabled(true);
        btnSendMsg.setEnabled(true);
    }

    public void disableChat() {
        tvChatEmptyHint.setText(mContext.getString(R.string.chat_is_closed));
        etChatMessage.setEnabled(false);
        btnSendMsg.setEnabled(false);
    }
}
