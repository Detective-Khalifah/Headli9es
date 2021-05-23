package project.android.headli9es;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import project.android.headli9es.databinding.ActivityMainBinding;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 0;
    private static final String LOG_TAG = MainActivity.class.getName();

    private final String NY_TIMES_API = "Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private String NY_TimesSection = "home";

    private static final String GUARDIAN_API_BASE_URL = "https://";
    private static final String NY_TIMES_BASE_URL = "https://api.nytimes.com/svc/topstories/v2/";
    private static final String NEWS_API_BASE_URL = "https://";

    private NewsAdapter newsAdapter;
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        // Create a new {@link NewsPopulator} that takes an empty, non-null {@link ArrayList} of
        // {@link News} as input.
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

        // TODO: Get url from Preference.
        String code = "NY_TIMES";
        Bundle seek = generateURL(code);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, seek,
                    (LoaderManager.LoaderCallbacks) MainActivity.this);
        } else {
            mMainBinding.pbNews.setVisibility(View.GONE);
            // TODO: Set up Snackbar here.
            Snackbar.make(mMainBinding.frameSnack, "No net access!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.api_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.news_settings_menu) {
            startActivity(new Intent(this, ConfigActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Register onPreferenceChangeListener and call generateURL() every time the
    //  {@link Activity} is resumed to make a request to the appropriate server. Unregister the
    //  listener otherwise.

    @Override
    protected void onPause () {
        super.onPause();
    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    private Bundle generateURL (String apiCode) {
        // TODO: Use preferences to make appropriate validation of {@link Bundle} content.
        Bundle seek = new Bundle();
        Uri base;
        Uri.Builder uriBuilder;

        switch (apiCode) {
            case "NY_TIMES":
                Log.i(LOG_TAG, "NY_Times api selected.");
                seek.putString("code", apiCode);

                base = Uri.parse(NY_TIMES_BASE_URL);
                uriBuilder = base.buildUpon();
                uriBuilder.appendPath(apiCode);

                // Attach parsed New York Times API {@link URL} to bundle.
                seek.putString("link", "https://api.nytimes.com/svc/topstories/v2/" + NY_TimesSection
                        + ".json?api-key=" + NY_TIMES_API);
                break;
            case "News_Org":
                Log.i(LOG_TAG, "newsapi.org api selected.");
                seek.putString("code", apiCode);

                base = Uri.parse(NEWS_API_BASE_URL);
                uriBuilder = base.buildUpon();
                uriBuilder.appendPath(apiCode);

                // Attach parsed newsapi.org API {@link URL} to bundle.
                seek.putString("link", uriBuilder.toString());
                break;
            default:
                Log.i(LOG_TAG, "Default api chosen.");
                seek.putString( "code", apiCode);

                base = Uri.parse(NY_TIMES_BASE_URL);
                uriBuilder = base.buildUpon();
                uriBuilder.appendPath(apiCode);

                // Attach parsed Default news API {@link URL} to bundle.
                seek.putString("link", "");
        }
        return seek;
    }

    @Override
    public Loader<List<News>> onCreateLoader (int i, final Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader() called");
        return new NewsLoader(this, bundle);
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

            // TODO: Find a way to set it automatically.
            mMainBinding.tvNoa.setVisibility(View.VISIBLE);
            mMainBinding.tvArticlesCount.numArticles.setText(getResources().getQuantityString(
                    R.plurals.articles_count,
                    data.get(0).getArticlesNumber(),
                    data.get(0).getArticlesNumber())
            );
            Log.i(LOG_TAG, "Number of articles(List): " + data.get(0).getArticlesNumber());

            newsAdapter.addAll(data);
        } else {
            mMainBinding.tvNoa.setText(R.string.no_article_fetched);
            mMainBinding.tvNoa.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter.notifyDataSetInvalidated();
    }

}