package android.example.com.mynewsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.com.mynewsapp.R;
import android.example.com.mynewsapp.models.Article;
import android.example.com.mynewsapp.models.BaseModel;
import android.example.com.mynewsapp.models.Section;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by ByteTonight on 05.06.2017.
 */

public class Utils {
    public static final String LOG_TAG = Utils.class.getName();
    public static final String KEY_RESPONSE = "response";
    public static final String KEY_RESPONSE_STATUS = "status";
    public static final String ARRAY_KEY_RESULTS = "results";
    public static final String KEY_ID = "id";
    public static final String KEY_ARTICLE_TYPE = "type";
    public static final String KEY_SECTION_ID = "sectionId";
    public static final String KEY_SECTION_NAME = "sectionName";
    public static final String KEY_WEB_PUBLICATION_DATE = "webPublicationDate";
    public static final String KEY_WEB_TITLE = "webTitle";
    public static final String KEY_WEB_URL = "webUrl";
    public static final String KEY_API_URL = "ApiTitle";
    public static final String KEY_IS_HOSTED = "isHosted";


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            int resposeCode = urlConnection.getResponseCode();
            switch (resposeCode) {
                case 200:
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                    break;
                default:
                    Log.e(LOG_TAG, "Server responded with :" + resposeCode);
                    //do something
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error trying to fetch JSON data", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static ArrayList<Article> getArticlesFromJSON(final Context context, String response) {
        // If the JSON string is empty or null, then return early.
        if (response.isEmpty()) {
            return null;
        }


        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Article> articles = new ArrayList<>();
        JSONObject root = null;
        JSONObject responseNode = null;
        try {
            root = new JSONObject(response);

            if (root.has(KEY_RESPONSE))
                responseNode = root.getJSONObject(KEY_RESPONSE);
            //Uncomment the 2 lines below to raise a JSONException, and see how it's handled
            //if (true)
            //throw new JSONException("Some Element was not found... yada");
            if (responseNode.has(KEY_RESPONSE_STATUS)) {
                String status = responseNode.getString(KEY_RESPONSE_STATUS);
                if (!status.equalsIgnoreCase("ok")) {
                    //Something went wrong
                }

            }
            if (responseNode.has(ARRAY_KEY_RESULTS)) {
                JSONArray articlesArray = responseNode.getJSONArray(ARRAY_KEY_RESULTS); //JSON Node named items
                int articlesArrayLength = articlesArray.length();
                for (int i = 0; i < articlesArrayLength; ++i) {

                    JSONObject curArt = articlesArray.getJSONObject(i);

                    String id = curArt.has(KEY_ID) ?
                            curArt.getString(KEY_ID) : "";
                    String sectionName = curArt.has(KEY_SECTION_NAME) ?
                            curArt.getString(KEY_SECTION_NAME) : "";

                    String webUrl = curArt.has(KEY_WEB_URL) ?
                            curArt.getString(KEY_WEB_URL) : "";

                    String webTitle = curArt.has(KEY_WEB_TITLE) ?
                            curArt.getString(KEY_WEB_TITLE) : "";


                    Article article = new Article(context);
                    article.setWebTitle(webTitle);
                    article.setSectionName(sectionName);
                    article.setWebUrl(webUrl);
                    articles.add(article);
                }
            }

        } catch (final JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.json_error), e);
            EventBus.getDefault().post(new
                    MessageEvent(context.getString(R.string.json_error) +
                    "\n" + e.getMessage()));


        }
        return articles;
    }

    public static ArrayList<Section> getSectionsFromJSON(final Context context, String response) {
        // If the JSON string is empty or null, then return early.
        if (response.isEmpty()) {
            return null;
        }


        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Section> sections = new ArrayList<>();
        JSONObject root = null;
        JSONObject responseNode = null;
        try {
            root = new JSONObject(response);

            if (root.has(KEY_RESPONSE))
                responseNode = root.getJSONObject(KEY_RESPONSE);
            //Uncomment the 2 lines below to raise a JSONException, and see how it's handled
            //if (true)
            //throw new JSONException("Some Element was not found... yada");
            if (responseNode.has(KEY_RESPONSE_STATUS)) {
                String status = responseNode.getString(KEY_RESPONSE_STATUS);
                if (!status.equalsIgnoreCase("ok")) {
                    //Something went wrong
                }

            }
            if (responseNode.has(ARRAY_KEY_RESULTS)) {
                JSONArray articlesArray = responseNode.getJSONArray(ARRAY_KEY_RESULTS); //JSON Node named items
                int articlesArrayLength = articlesArray.length();
                for (int i = 0; i < articlesArrayLength; ++i) {

                    JSONObject curArt = articlesArray.getJSONObject(i);


                    String webTitle = curArt.has(KEY_WEB_TITLE) ?
                            curArt.getString(KEY_WEB_TITLE) : "";


                    Section section = new Section(context);
                    section.setWebTitle(webTitle);

                    sections.add(section);
                }
            }

        } catch (final JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.json_error), e);
            EventBus.getDefault().post(new
                    MessageEvent(context.getString(R.string.json_error) +
                    "\n" + e.getMessage()));


        }
        return sections;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return isWiFi;
    }

    public static boolean hasConnection(Context context) {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Store key,value pairs in Android Shared Preferences
     *
     * @param key   to store
     * @param value to store
     */
    public static void writeStringToPreferences(Context context, String key, String value) {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        Log.v("writePreferences", key + " : " + value);
    }


    /**
     * Read key,value pairs from Android Shared Preferences
     *
     * @param key to read
     * @return
     */
    public static String readStringFromPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String returnData = sharedPref.getString(key, null);
        //Let's see what we got from shared preferences
        Log.v("readPreferences", key + " = " + returnData);
        return returnData;
    }

}
