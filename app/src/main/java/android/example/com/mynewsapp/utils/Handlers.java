package android.example.com.mynewsapp.utils;

import android.content.Context;
import android.content.Intent;
import android.example.com.mynewsapp.models.Article;
import android.net.Uri;
import android.view.View;


/**
 * Created by ByteTonight on 05.06.2017.
 */

public class Handlers {

    public void onClickViewDetails(View v, Article article) {
        Context context = v.getContext();

        Intent browse = new Intent(Intent.ACTION_VIEW);
        browse.setData(Uri.parse(article.getWebUrl()));
        context.startActivity(browse);
    }




}
