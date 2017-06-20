package android.example.com.mynewsapp.models;

import android.content.Context;

/**
 * Created by ByteTonight on 19.06.2017.
 */

public abstract class BaseModel {
    protected Context context;

    public BaseModel(Context context) {
        this.context = context;
    }
}
