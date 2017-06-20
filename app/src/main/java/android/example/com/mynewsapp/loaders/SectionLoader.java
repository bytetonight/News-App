package android.example.com.mynewsapp.loaders;

import android.content.Context;
import android.example.com.mynewsapp.models.Section;
import android.example.com.mynewsapp.utils.Utils;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by ByteTonight on 10.06.2017.
 */

public class SectionLoader extends AsyncTaskLoader<List<Section>> {

    String sourceUrl;


    public SectionLoader(Context context, String url) {
        super(context);
        sourceUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Section> loadInBackground() {
        if (sourceUrl == null) {
            return null;
        }
        // Create URL object
        URL url = Utils.createUrl(sourceUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = Utils.makeHttpRequest(url);
        } catch (IOException e) {

        }

        // Extract relevant fields from the JSON response and create an {@link Event} object

        List<Section> books = Utils.getSectionsFromJSON(getContext(), jsonResponse);

        return books;
    }


}
