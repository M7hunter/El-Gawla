package it_geeks.info.gawla_app.views.salonUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Activity;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.SalonActivity;

public class SocketUtils {

    private static final String GAWLA_SERVER_URL = "http://134.209.0.250:8888";
    private static final String TAG = "socket_connection";
    private Socket socket;
    private Context mContext;

    private Round round;
    private ChatUtils chatUtils;
    private ArrayList<String> names = new ArrayList<>();
    private boolean isOn = false;

    public SocketUtils(Context context) {
        Log.d(TAG, "getInstance: new");
        mContext = context;
        round = ((SalonActivity) mContext).getRound();
        chatUtils = ((SalonActivity) mContext).getChatUtils();
    }

    private void initSocket() {
        if (socket == null)
            try {
                socket = IO.socket(GAWLA_SERVER_URL);
            } catch (URISyntaxException e) {
                Crashlytics.logException(e);
                throw new RuntimeException(e);
            }
    }

    public void connectSocket() {
        if (socket == null || !socket.connected()) {
            initSocket();
            socket.connect();
            Log.d(TAG, "connectSocket: connected");
            if (!isOn) {
                handleSocketEvents();
                isOn = true;
            }
        }
    }

    public void emitData(String event, JSONObject obj) {
        if (socket != null && socket.connected()) {
            socket.emit(event, obj);
            Log.d(TAG, "emitData: " + event);
        }
    }

    public void disconnectSocket() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            Log.d(TAG, "disconnectSocket: disconnected");
            isOn = false;
        }
    }

    private void handleSocketEvents() {
        Log.d(TAG, "handleSocketEvents: connected::" + socket.connected());
        socket.emit("allActivity", round.getSalon_id()); // what action triggers this emit ?!
        ((SalonActivity) mContext).initActivityRecycler();

        try {
            JSONObject o = new JSONObject();
            o.put("room", round.getSalon_id());
            o.put("user", SharedPrefManager.getInstance(mContext).getUser().getName());
            socket.emit("joinRoom", o);
        } catch (JSONException e) {
            Log.e("socket joinRoom: ", e.getMessage());
            Crashlytics.logException(e);
        }

        socket.on("new_member", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        } catch (Exception e) {
                            Log.e("socket newMember: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("member_add_offer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        } catch (Exception e) {
                            Log.e("socket memberAddOffer: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("member_leave", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        } catch (Exception e) {
                            Log.e("socket memberLeave: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("winner", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        } catch (Exception e) {
                            Log.e("socket winner: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("activity", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray main = (JSONArray) args[0];
                            for (int i = 0; i < main.length(); i++) {
                                JSONObject jsonObject = main.getJSONObject(i);
                                ((SalonActivity) mContext).updateActivityList(new Activity(jsonObject.get("activity").toString(), jsonObject.get("created_at").toString()));
                            }
                        } catch (Exception e) {
                            Log.e("socket activity: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("member_use_card", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        } catch (Exception e) {
                            Log.e("socket memberUseCard: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("isTyping", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = (JSONObject) args[0];
                            String user = obj.getString("user_name");
//                            int user_id = obj.getInt("user_id");
//                            Log.d(TAG, "user_id: " + user_id);

                            if (!names.contains(user)) {
                                names.add(user);
                            }

//                            if (user_id != SharedPrefManager.getInstance(mContext).getUser().getUser_id()) {
                                if (names.size() > 1) {
                                    chatUtils.tvChatTypingState.setText(mContext.getString(R.string.is_typing_others, names.get(0)));
                                } else {
                                    chatUtils.tvChatTypingState.setText(mContext.getString(R.string.is_typing, names.get(0)));
                                }
                                chatUtils.tvChatTypingState.setVisibility(View.VISIBLE);
//                            }
                        } catch (JSONException e) {
                            Log.e("socket SendTypingState", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("stopTyping", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String user = data.getString("user_name");

                            if (names.contains(user)) {
                                names.remove(user);
                                if (names.size() == 0) {
                                    chatUtils.tvChatTypingState.setVisibility(View.GONE);
                                    chatUtils.tvChatTypingState.setText("");
                                } else {
                                    if (names.size() > 1) {
                                        chatUtils.tvChatTypingState.setText(mContext.getString(R.string.is_typing, names.get(0)));
                                    } else {
                                        chatUtils.tvChatTypingState.setText(mContext.getString(R.string.is_typing, names.get(0)));
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            Log.e("socket SendTypingState", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject main = (JSONObject) args[0];
                        int user_id;
                        String user_name;
                        String message;
                        String date;
                        try {
                            JSONObject data = main.getJSONObject("message");
                            user_id = data.getInt("user_id");
                            user_name = data.getString("user_name");
                            message = data.getString("message");
                            date = data.getString("date");
                            chatUtils.addMessageToChat(user_id, user_name, message, date);
                            ((SalonActivity) mContext).tvChatEmptyHint.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            Log.e("socket message", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        });
    }
}
