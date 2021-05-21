package project.android.headli9es;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import project.android.headli9es.databinding.ActivityMainBinding;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 0;
    private static final String LOG_TAG = MainActivity.class.getName();

    private final String NY_TIMES_API = "Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private String NY_TimesSection = "home";

    private NewsAdapter newsAdapter;
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        // Create a new {@link NewsPopulator} that takes an empty, non-null {@link ArrayList} of
        // {@link News} as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        mMainBinding.listView.setAdapter(newsAdapter);
        mMainBinding.listView.setEmptyView(mMainBinding.tvNoa);
        mMainBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ACTION_VIEW,
                        Uri.parse(newsAdapter.getItem(position).getPage()))
                );
            }
        });

        mMainBinding.tvArticlesCount.numArticles.setVisibility(View.GONE);

        Bundle seek = new Bundle();
        seek.putString("link", "https://api.nytimes.com/svc/topstories/v2/" + NY_TimesSection
                + ".json?api-key=" + NY_TIMES_API);

        LoaderManager loaderManager = getSupportLoaderManager();
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
        mMainBinding.tvArticlesCount.numArticles.setVisibility(View.VISIBLE);

        // TODO: LiveData + ViewModel; Pull-to-Refresh.
        newsAdapter.notifyDataSetChanged();

        // If there is a valid list of {@link News}, then add them to the {@link NewsAdapter}'s dataset.
        // This will trigger the {@link ListView} to update.
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "Data not empty in onPostExecute's check");

            newsAdapter.addAll(data);

            // TODO: Find a way to set it automatically.
            mMainBinding.tvArticlesCount.numArticles.setText(getResources().getQuantityString(
                    R.plurals.articles_count,
                    data.get(0).getArticlesNumber(),
                    data.get(0).getArticlesNumber())
            );
            Log.i(LOG_TAG, "Number of articles(List): " + data.get(0).getArticlesNumber());
        } else {
            mMainBinding.tvNoa.setText(R.string.no_article_fetched);
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter.notifyDataSetInvalidated();
    }

}