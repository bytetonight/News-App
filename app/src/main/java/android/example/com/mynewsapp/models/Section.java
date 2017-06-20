package android.example.com.mynewsapp.models;

import android.content.Context;

/**
 * Created by ByteTonight on 19.06.2017.
 */

public class Section extends BaseModel {

    private String id;
    private String webTitle;
    private String webUrl;
    private String apiUrl;

    public Section(Context c) {
        super(c);
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }


    @Override
    public String toString() {
        return webTitle;
    }
}
