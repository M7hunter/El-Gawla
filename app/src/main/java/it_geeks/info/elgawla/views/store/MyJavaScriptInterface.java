package it_geeks.info.elgawla.views.store;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class MyJavaScriptInterface {

    static JSONObject paymentObj;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        new extractJsonAsyncTask().execute(html);
    }

    static class extractJsonAsyncTask extends AsyncTask<String, String, JSONObject> {

        private List<Character> stack;
        private List<String> jsonList;
        private String temp = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            stack = new ArrayList<>();
            jsonList = new ArrayList<>();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            for (char eachChar : strings[0].toCharArray()) {
                if (stack.isEmpty() && eachChar == '{') {
                    stack.add(eachChar);
                    temp += eachChar;
                } else if (!stack.isEmpty()) {
                    temp += eachChar;
                    if (stack.get(stack.size() - 1).equals('{') && eachChar == '}') {
                        stack.remove(stack.size() - 1);
                        if (stack.isEmpty()) {
                            jsonList.add(temp);
                            temp = "";
                        }
                    } else if (eachChar == '{' || eachChar == '}')
                        stack.add(eachChar);
                } else if (temp.length() > 0 && stack.isEmpty()) {
                    jsonList.add(temp);
                    temp = "";
                }
            }

//            for (String jo : jsonList)
//                Log.d("json_obj", jo);

            try {
                JSONObject jo = new JSONObject(jsonList.get(0));
                return jo.getJSONObject("payment");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            paymentObj = jsonObject;
        }
    }
}
