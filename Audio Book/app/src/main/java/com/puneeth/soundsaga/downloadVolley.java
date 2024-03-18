package com.puneeth.soundsaga;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class downloadVolley {

    private static final String TAG = "downloadVolley";
    private static final String dataUrl ="https://christopherhield.com/ABooks/abook_contents.json";

    private static ArrayList<Book> bookList=new ArrayList<>();




//    public static void downloadBooks(MainActivity mainActivity) {
//
//        RequestQueue queue = Volley.newRequestQueue(mainActivity);
//
//        Uri.Builder buildURL = Uri.parse(dataUrl).buildUpon();
//
//        String urlToUse = buildURL.build().toString();
//
//        Response.Listener<JSONObject> listener = response -> {
//            try {
//                handleSuccess(response.toString(), mainActivity);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        };
//
//        Response.ErrorListener error = error1 -> handleFail(error1);
//
//        // Request a string response from the provided URL.
//        JsonObjectRequest jsonObjectRequest =
//                new JsonObjectRequest(Request.Method.GET, urlToUse,
//                        null, listener, error);
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest);
//    }

    public static void downloadBooks(MainActivity mainActivity) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(dataUrl).buildUpon();
        String urlToUse = buildURL.build().toString();

        // Adjusted to handle response as JSONArray directly if possible
        Response.Listener<JSONArray> listener = response -> {
            try {
                handleSuccess(response, mainActivity); // Adjusted to pass JSONArray directly
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = VolleyError::printStackTrace;

        // Adjust your request here to fetch a JSONArray
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlToUse, null, listener, error);
        queue.add(jsonArrayRequest);
    }

    private static void handleFail(VolleyError error1)
    {

        Log.d(TAG, "handleFail: "+error1);
    }

    private static void handleSuccess(JSONArray response,
                                      MainActivity mainActivity ) throws JSONException {


        Log.d(TAG, "length of Books array:" + response.length());

        for (int i = 0; i < response.length(); i++) {
            JSONObject temp = response.getJSONObject(i);
            String title = temp.getString("title");
            Log.d(TAG, "BookName: " + title);
            String author = temp.getString("author");
            String date = temp.getString("date");
            String language = temp.getString("language");
            String duration = temp.getString("duration");
            String image = temp.getString("image");

            //getting the chapters below
            ArrayList<Chapter> bookChapters = new ArrayList<>();
            JSONArray allchapters = temp.getJSONArray("contents");
            Log.d(TAG, "Chapters in Book: " + allchapters.length());
            for (int j = 0; j < allchapters.length(); j++) {
                JSONObject temp2 = allchapters.getJSONObject(j);
                String num = temp2.getString("number");
                int number = Integer.parseInt(num);
                String title2 = temp2.getString("title");
                String url = temp2.getString("url");
                Chapter chap = new Chapter(number, title2, url);
               // Log.d(TAG, "chapter details-"+j+":"+chap.toString());
                bookChapters.add(chap);
            }
            Book b = new Book(title, author, date, language, duration, image, bookChapters);
            Log.d(TAG, "book added: "+b.toString());
            bookList.add(b);

        }
        mainActivity.runOnUiThread(() -> mainActivity.acceptDownload(bookList));


    }
}
