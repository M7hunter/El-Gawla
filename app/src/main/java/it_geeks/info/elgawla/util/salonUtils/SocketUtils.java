package it_geeks.info.elgawla.util.salonUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import it_geeks.info.elgawla.repository.Models.Activity;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.salon.SalonActivity;

public class SocketUtils {

    private static final String GAWLA_SERVER_URL = "http://134.209.0.250:8888";
    //    private static final String GAWLA_SERVER_URL = "http://192.168.1.8:8888";
    private static final String TAG = "socket_connection";
    private Socket socket;
    private Context mContext;

    private Salon salon;
    private ChatUtils chatUtils;
    private boolean isOn = false;

    public SocketUtils(Context context) {
        Log.d(TAG, "getInstance: new");
        mContext = context;
        salon = ((SalonActivity) mContext).getSalon();
        chatUtils = ((SalonActivity) mContext).getChatUtils();
    }

    private void initSocket() {
        if (socket == null)
            try
            {
                socket = IO.socket(GAWLA_SERVER_URL);
            }
            catch (URISyntaxException | RuntimeException e)
            {
                Crashlytics.logException(e);
            }
    }

    public void connectSocket() {
        if (socket == null || !socket.connected())
        {
            initSocket();
            socket.connect();
            Log.d(TAG, "connectSocket: connected");
            if (!isOn)
            {
                handleSocketEvents();
                isOn = true;
            }
        }
    }

    public void emitData(String event, JSONObject obj) {
        try
        {
            if (socket != null)
            {
                socket.emit(event, obj);
                Log.d(TAG, "emitData: " + event);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void disconnectSocket() {
        if (socket != null && socket.connected())
        {
            socket.disconnect();
            Log.d(TAG, "disconnectSocket: disconnected");
            isOn = false;
        }
    }

    private void handleSocketEvents() {
        Log.d(TAG, "handleSocketEvents: connected::" + socket.connected());
        socket.emit("allMessages", salon.getSalon_id());

        try
        {
            JSONObject obj = new JSONObject();
            obj.put("salon_id", salon.getSalon_id());
            obj.put("lang", SharedPrefManager.getInstance(mContext).getSavedLang());
            emitData("allActivity", obj);
            ((SalonActivity) mContext).initActivityRecycler();

            JSONObject o = new JSONObject();
            o.put("room", salon.getSalon_id());
            o.put("user", SharedPrefManager.getInstance(mContext).getUser().getName());
            o.put("lang", SharedPrefManager.getInstance(mContext).getSavedLang());
            emitData("joinRoom", o);
        }
        catch (JSONException e)
        {
            Log.e("socket joinRoom: ", e.getCause().toString());
            Crashlytics.logException(e);
        }

        socket.on("activity", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            JSONArray main = (JSONArray) args[0];
                            for (int i = 0; i < main.length(); i++)
                            {
                                JSONObject jsonObject = main.getJSONObject(i);
                                ((SalonActivity) mContext).updateActivityList(new Activity(jsonObject.get("activity").toString(), jsonObject.get("created_at").toString()));
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("socket activity: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("new_member", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        }
                        catch (Exception e)
                        {
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
                        try
                        {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        }
                        catch (Exception e)
                        {
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
                        try
                        {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        }
                        catch (Exception e)
                        {
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
                        try
                        {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));
                        }
                        catch (Exception e)
                        {
                            Log.e("socket winner: ", e.getMessage());
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
                        try
                        {
                            JSONObject main = (JSONObject) args[0];
                            ((SalonActivity) mContext).updateLatestActivity(main.get("data").toString());
                            ((SalonActivity) mContext).updateActivityList(new Activity(main.get("data").toString(), main.get("date").toString()));

                            // TODO: color time_container background with used card's color
                        }
                        catch (Exception e)
                        {
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
                        try
                        {
                            JSONObject obj = (JSONObject) args[0];
                            String user = obj.getString("user_name");

                            chatUtils.tvChatTypingState.setText(user);
                            chatUtils.tvChatTypingState.setVisibility(View.VISIBLE);
                        }
                        catch (JSONException e)
                        {
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
                        chatUtils.tvChatTypingState.setVisibility(View.GONE);
                        chatUtils.tvChatTypingState.setText("");
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
                        try
                        {
                            JSONObject data = main.getJSONObject("message");
                            user_id = data.getInt("user_id");
                            user_name = data.getString("user_name");
                            message = data.getString("message");
                            date = data.getString("date");
                            chatUtils.addMessageToChat(user_id, user_name, message, date);
                            ((SalonActivity) mContext).tvChatEmptyHint.setVisibility(View.GONE);
                        }
                        catch (JSONException e)
                        {
                            Log.e("socket message", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        }).on("messages", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((SalonActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            JSONArray mainArr = (JSONArray) args[0];
                            Log.d(TAG, "allMessages:: " + mainArr.toString());

                            for (int i = 0; i < mainArr.length(); i++)
                            {
                                JSONObject obj = mainArr.getJSONObject(i);
                                JSONObject msgObj = obj.getJSONObject("message");

                                int user_id = msgObj.getInt("user_id");
                                String user_name = msgObj.getString("user_name");
                                String message = msgObj.getString("message");
                                String date = msgObj.getString("date");
                                chatUtils.addMessageToChat(user_id, user_name, message, date);
                            }

                            ((SalonActivity) mContext).tvChatEmptyHint.setVisibility(View.GONE);
                        }
                        catch (JSONException e)
                        {
                            Log.e("socket message", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }
        });
    }
}
