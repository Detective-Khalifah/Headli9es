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
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import project.android.headli9es.databinding.ForecastBinding;

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
        Log.i(LOG_TAG, "LoaderManager initialised called::");
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

            mNewsProgress.setVisibility(View.GONE);
            mNewsRecycler.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mNewsRecycler.setLayoutManager(layoutManager);
            mNewsRecycler.setHasFixedSize(true);

            // Create a new newsPopulator that takes a rich (or otherwise empty) list of newsList as input
            newsAdapter = new NewsAdapter(data, MainActivity.this);
//            Log.i(LOG_TAG, "newsPopulator is ::: " + newsPopulator);

            mNewsRecycler.setAdapter(newsAdapter);
            Log.i(LOG_TAG, "Adapter set on Recycler:: " + newsAdapter);

//            binder.tvArticlesCount.numArticles.setText();
            Log.i(LOG_TAG, "news articles RecyclerView:: " + mNewsRecycler);
        } else {
            mNewsProgress.setVisibility(View.VISIBLE);
            mNewsRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset (Loader loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
//        newsPopulator = new NewsPopulator(new ArrayList<News>(), this, binder);
    }

    @Override
    public void onArticleClickListener (String link) {
        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri articleUri = Uri.parse(link);

        // Create a new intent to view the news URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);
    }
}