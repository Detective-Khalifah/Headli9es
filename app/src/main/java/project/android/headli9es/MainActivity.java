package project.android.headli9es;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import project.android.headli9es.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 0;
    private String LOG_TAG = MainActivity.class.getName(), section = "home", ap="Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private NewsAdapter newsAdapter;
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        LoaderManager loaderManager = getSupportLoaderManager();

        // Create a new {@link NewsPopulator} that takes an empty, non-null {@link ArrayList} of
        // {@link News} as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        mMainBinding.listView.setAdapter(newsAdapter);
        mMainBinding.listView.setEmptyView(mMainBinding.tvNoa);

        Bundle seek = new Bundle();
        seek.putString("link", "https://api.nytimes.com/svc/topstories/v2/" + section + ".json?api-key=" + ap);
        loaderManager.initLoader(LOADER_ID, seek,
                (LoaderManager.LoaderCallbacks) MainActivity.this);
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
        mMainBinding.pbNews.setVisibility(View.GONE);
        newsAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the listPopulator's dataset.
        // This will trigger the RecyclerView to update.
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "Data not empty in onPostExecute's check");

            newsAdapter.addAll(data);
        } else {
            mMainBinding.tvNoa.setText("0 articles could be fetched!");
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter.notifyDataSetInvalidated();
    }

}