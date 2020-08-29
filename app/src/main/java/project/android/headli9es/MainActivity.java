package project.android.headli9es;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<News>>,
        NewsAdapter.ArticleClickListener {

    private static final int LOADER_ID = 0;
    private String LOG_TAG = MainActivity.class.getName();
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

        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle seek = new Bundle();
        seek.putString("link", "https://newsapi.org/v2/top-headlines?country=ng&pageSize=25");
        loaderManager.initLoader(LOADER_ID, seek,
                (LoaderManager.LoaderCallbacks) MainActivity.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNewsRecycler.setLayoutManager(layoutManager);
        mNewsRecycler.setHasFixedSize(true);

        // Create a new newsPopulator that takes a rich (or otherwise empty) list of newsList as input
        newsAdapter = new NewsAdapter(new ArrayList<News>(), MainActivity.this);

        mNewsRecycler.setAdapter(newsAdapter);
    }

    @Override
    public Loader<List<News>> onCreateLoader (int i, final Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader() called");
        return new AsyncTaskLoader<List<News>>(this) {
            List<News> result;
            String address = bundle.getString("link");

            @Override
            public void deliverResult (List<News> data) {
                result = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading () {
                super.onStartLoading();
                if (result != null) {
                    deliverResult(result);
                } else
                    forceLoad();
            }

            @Override
            public List<News> loadInBackground () {
                Log.i(LOG_TAG, "This is loadInBackground. I received: " + address);

                    // Don't perform the request if there are no URLs, or the first URL is null.
                    if (address == null) {
                        Log.i(this.getClass().getName(), "Conditional check finds null");
                        return null;
                    } else {
                        result = Search.lookUpVolumes(bundle.getString("link"));
                        Log.i(this.getClass().getName(), "result List data: " + result);
                        return result;
                    }
                }
        };
    }

    @Override
    public void onLoadFinished (androidx.loader.content.Loader<List<News>> loader, List<News> data) {
        // If there is a valid list of {@link News}, then add them to the listPopulator's dataset.
        // This will trigger the RecyclerView to update.
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "Data not empty in onPostExecute's check");

            mNewsProgress.setVisibility(View.GONE);
            mNewsRecycler.setVisibility(View.VISIBLE);

            newsAdapter = new NewsAdapter(data, MainActivity.this);
            mNewsRecycler.setAdapter(newsAdapter);
        } else {
            mNewsProgress.setVisibility(View.VISIBLE);
            mNewsRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter = new NewsAdapter(new ArrayList<News>(), this);
    }

    @Override
    public void onArticleClickListener (String link) {
        // An implicit intent to open the article link in a browser
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}