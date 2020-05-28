package project.android.headli9es;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 0;
    private String LOG_TAG = MainActivity.class.getName();
    private LoaderManager loaderManager = getLoaderManager();
    static ListPopulator listPopulator;
    static TextView nullNEWS;
    ListView newsArticles;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.i(LOG_TAG, "App has launched, Khal!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
        Log.i(LOG_TAG, "LoaderManager initialised called::");

//        nullNEWS = findViewById(R.id.status);
//        nullNEWS.setText(R.string.matches0);

        // Create a new listPopulator that takes a rich (or otherwise empty) list of newsList as input
        listPopulator = new ListPopulator(this, new ArrayList<News>());
        Log.i(LOG_TAG, "listPopulator is ::: " + listPopulator);

        newsArticles = findViewById(R.id.articles_page);
        newsArticles.setAdapter(listPopulator);
        Log.i(LOG_TAG, "Adapter set on ListView:: " + newsArticles);

//        newsArticles.setEmptyView(nullNEWS);

        newsArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(LOG_TAG, "newsArticles onItemClickListener");
                // Find the current article that was clicked on
                News currentArticle = listPopulator.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getPage());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    @Override
    public Loader<List<News>> onCreateLoader (int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader() called");
        return new NewsLoader(this, "https://newsapi.org/v2/top-headlines?country=ng");
    }

    @Override
    public void onLoadFinished (Loader<List<News>> loader, List<News> data) {
        Log.i(LOG_TAG, "onLoadFinished() called");

        // If there is a valid list of {@link Book}s, then add them to the listPopulator's dataset. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "Data not empty in onPostExecute's check");

//            nullNEWS.setVisibility(View.GONE);

            // Get the list of newsList from {@link Search}
            newsArticles = findViewById(R.id.articles_page);
            Log.i(LOG_TAG, "bookEntries ListView ::: " + newsArticles);

            listPopulator.clear();
            Log.i(LOG_TAG, "listPopulator cleared.");

            listPopulator.addAll(data);
            Log.i(LOG_TAG, "listPopulator.addAll executed");
        } else {
//            nullNEWS.setVisibility(View.VISIBLE);
//            nullNEWS.setText(R.string.matches0);
        }
    }

    @Override
    public void onLoaderReset (Loader loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        listPopulator = new ListPopulator(this, new ArrayList<News>());
        listPopulator.clear();
    }
}