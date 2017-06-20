package android.example.com.mynewsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.example.com.mynewsapp.adapters.ArticleAdapter;
import android.example.com.mynewsapp.loaders.ArticleLoader;
import android.example.com.mynewsapp.loaders.SectionLoader;
import android.example.com.mynewsapp.models.Article;
import android.example.com.mynewsapp.models.Section;
import android.example.com.mynewsapp.utils.Config;
import android.example.com.mynewsapp.utils.MessageEvent;
import android.example.com.mynewsapp.utils.Utils;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //This Activity has 2 AsyncTaskLoader implementations ... yup
    private LoaderManager.LoaderCallbacks<List<Article>> articleLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Article>>() {

        @Override
        public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
            if (args.isEmpty())
                return null;
            loadingIndicator.setVisibility(View.VISIBLE);
            return new ArticleLoader(MainActivity.this, args.getString("Uri"));
        }


        @Override
        public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
            loadingIndicator.setVisibility(View.GONE);
            if (data == null || data.isEmpty()) {
                articles.clear();
                emptyText.setText(R.string.strNoRecordsFound);
            } else {
                MainActivity.this.articles = data;
                prepareArticlesRecyclerView();
            }
            articleAdapter.notifyDataSetChanged();
        }


        @Override
        public void onLoaderReset(Loader<List<Article>> loader) {
            prepareArticlesRecyclerView();
        }
    };


    private LoaderManager.LoaderCallbacks<List<Section>> sectionsLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Section>>() {

        @Override
        public Loader<List<Section>> onCreateLoader(int id, Bundle args) {
            if (args.isEmpty())
                return null;
            loadingIndicator.setVisibility(View.VISIBLE);
            return new SectionLoader(MainActivity.this, args.getString("Uri"));
        }


        @Override
        public void onLoadFinished(Loader<List<Section>> loader, List<Section> data) {
            loadingIndicator.setVisibility(View.GONE);
            if (data == null || data.isEmpty()) {
                sections.clear();
                emptyText.setText(R.string.strNoRecordsFound);
            } else {
                Log.v("###", "onLoadFinished");
                MainActivity.this.sections = data;
                showConfigDialog();
                //For unknown reasons the first time the dialog opened, onLoadFinished fired once,
                //every other time onLoadFinished fired up twice, hence the line below
                loaderManager.destroyLoader(SECTION_LOADER_ID);
            }

        }


        @Override
        public void onLoaderReset(Loader<List<Section>> loader) {

        }
    };

    private Section currentSection = null;
    private LoaderManager loaderManager;

    private static final int ARTICLE_LOADER_ID = 111;
    private static final int SECTION_LOADER_ID = 222;
    private static boolean firstRun = true;
    private List<Article> articles = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private TextView emptyText;
    private TextView errorText;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.options:
                loadSections();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager = getSupportLoaderManager();

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        emptyText = (TextView) findViewById(R.id.empty);
        errorText = (TextView) findViewById(R.id.error);
        errorText.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        emptyText.setText(R.string.strNoRecordsFound);
        emptyText.setVisibility(View.GONE);

        prepareArticlesRecyclerView();
        String currentSectionString = Utils.readStringFromPreferences(this, "currentSection");
        if (currentSectionString == null || currentSectionString.isEmpty())
            currentSectionString = "politics";
        loadArticles(currentSectionString);


    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    private void loadArticles(String needle) {


        Uri baseUri = Uri.parse(Config.API_SEARCH_ENDPOINT);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(Config.PARAM_QUERY, needle);
        uriBuilder.appendQueryParameter(Config.PARAM_API_KEY, Config.PARAM_API_KEY_VALUE);

        Bundle args = new Bundle();
        args.putString(Config.PARAM_URI, uriBuilder.toString());

        loaderManager.initLoader(ARTICLE_LOADER_ID, args, articleLoaderListener);
        if (loaderManager.getLoader(ARTICLE_LOADER_ID).isStarted())
            loaderManager.restartLoader(ARTICLE_LOADER_ID, args, articleLoaderListener);

    }

    private void loadSections() {

        Uri baseUri = Uri.parse(Config.API_SECTIONS_ENDPOINT);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(Config.PARAM_API_KEY, Config.PARAM_API_KEY_VALUE);

        Bundle args = new Bundle();
        args.putString(Config.PARAM_URI, uriBuilder.toString());

        loaderManager.initLoader(SECTION_LOADER_ID, args, sectionsLoaderListener);
        if (loaderManager.getLoader(SECTION_LOADER_ID).isStarted())
            loaderManager.restartLoader(SECTION_LOADER_ID, args, sectionsLoaderListener);

    }

    private void prepareArticlesRecyclerView() {
        articleAdapter = new ArticleAdapter(articles);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(articleAdapter);
    }

    private AdapterView.OnItemSelectedListener sectionSelectListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currentSection = (Section)parent.getItemAtPosition(position);
            Utils.writeStringToPreferences(MainActivity.this, "currentSection", currentSection.toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void showConfigDialog() {

        Log.v("###","showConfigDialog");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.options_dialog, null);


        Spinner sectionSpinner = (Spinner) dialogView.findViewById(R.id.sectionSpinner);
        sectionSpinner.setOnItemSelectedListener(sectionSelectListener);

        ArrayAdapter<Section> sectionArrayAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, sections);
        sectionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionArrayAdapter);


        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.dlg_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                loadArticles(currentSection.toString());
            }
        });
        dialogBuilder.setNegativeButton(R.string.dlg_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(MainActivity.this, event.message, Toast.LENGTH_SHORT).show();
    }
}
