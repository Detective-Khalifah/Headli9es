package project.android.headli9es;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<News>>,
        NewsAdapter.ArticleClickListener {

    private static final int LOADER_ID = 0;
    private String LOG_TAG = MainActivity.class.getName();
    private LoaderManager loaderManager = getLoaderManager();
    private NewsAdapter newsAdapter;
    static TextView nullNEWS;
    private ProgressBar mNewsProgress;
    protected RecyclerView mNewsRecycler;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsProgress = (ProgressBar) findViewById(R.id.pb_news);
        mNewsRecycler = (RecyclerView) findViewById(R.id.recycler);

        loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
    }

    @Override
    public Loader<List<News>> onCreateLoader (int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader() called");
        return new NewsLoader(this, "https://newsapi.org/v2/top-headlines?country=ng&pageSize=25");
    }

    @Override
    public void onLoadFinished (Loader<List<News>> loader, List<News> data) {
        // If there is a valid list of {@link News}, then add them to the listPopulator's dataset.
        // This will trigger the RecyclerView to update.
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "Data not empty in onPostExecute's check");

            mNewsProgress.setVisibility(View.GONE);
            mNewsRecycler.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mNewsRecycler.setLayoutManager(layoutManager);
            mNewsRecycler.setHasFixedSize(true);

            // Create a new newsPopulator that takes a rich (or otherwise empty) list of newsList as input
            newsAdapter = new NewsAdapter(data, MainActivity.this);

            mNewsRecycler.setAdapter(newsAdapter);
        } else {
            mNewsProgress.setVisibility(View.VISIBLE);
            mNewsRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset (Loader loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter = new NewsAdapter(new ArrayList<News>(), this);
    }

    @Override
    public void onArticleClickListener (String link) {
        // An implicit intent to open the article link in a browser
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}