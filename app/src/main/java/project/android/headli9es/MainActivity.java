package project.android.headli9es;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 0;
    private String LOG_TAG = MainActivity.class.getName(), section = "home", ap="Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private NewsAdapter newsAdapter;
    private TextView nullNEWS;
    private ProgressBar mNewsProgress;
    protected ListView mNewsList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nullNEWS = findViewById(R.id.tv_noa);
        mNewsProgress = findViewById(R.id.pb_news);
        mNewsList = findViewById(R.id.list_view);

        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle seek = new Bundle();
        seek.putString("link", "https://api.nytimes.com/svc/topstories/v2/" + section + ".json?api-key=" + ap);
        loaderManager.initLoader(LOADER_ID, seek,
                (LoaderManager.LoaderCallbacks) MainActivity.this);

        // Create a new newsPopulator that takes a rich (or otherwise empty) list of newsList as input
        newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());

        mNewsList.setAdapter(newsAdapter);
    }

    private void setEmptyView () {
        nullNEWS.setVisibility(View.VISIBLE);
        mNewsProgress.setVisibility(View.GONE);
        mNewsList.setVisibility(View.GONE);
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
            mNewsList.setVisibility(View.VISIBLE);

            newsAdapter = new NewsAdapter(MainActivity.this, data);
            mNewsList.setAdapter(newsAdapter);
        } else {
            setEmptyView();
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
    }

}