package android.example.com.mynewsapp.utils;

/**
 * Created by ByteTonight on 16.06.2017.
 */


/*
    https://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test
*/
public class Config {
    public static final String PARAM_URI = "Uri";
    public static final String PARAM_API_KEY = "api-key";
    public static final String PARAM_API_KEY_VALUE = "test";
    public static final String PARAM_QUERY = "q";
    private static final String API_URL = "https://content.guardianapis.com/";
    public static final String API_SECTIONS_ENDPOINT = API_URL + "sections";
    public static final String API_SEARCH_ENDPOINT = API_URL + "search"; // ?q=debates
}
