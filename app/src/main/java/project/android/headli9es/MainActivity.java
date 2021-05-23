package project.android.headli9es;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.snackbar.Snackbar;

import java.net.URL;
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
        LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LOADER_ID = 0;
    private static final String LOG_TAG = MainActivity.class.getName();

    /** API Base {@link URL}s for the 3 {@link News} outlets. */
    private static final String GUARDIAN_API_BASE_URL = "https://content.guardianapis.com/";
    private static final String NY_TIMES_BASE_URL = "https://api.nytimes.com/svc/topstories/v2/";
    private static final String NEWS_API_BASE_URL = "https://newsapi.org/v2/";

    /** APIs' parameters & paths */
    private static final String GUARDIAN_DEFAULT_PATH = "search";
    private static final String NEWS_DEFAULT_PATH = "top-headlines";
    private static final String NY_TIMES_DEFAULT_PATH = "";
    private final String NY_TIMES_API = "Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private String NY_TimesSection = "home";

    private static String DEFAULT_OUTLET, NEWS_OUTLET_PREFERNCE_KEY, PAGE_SIZE_PREFERENCE_KEY;

    private NewsAdapter newsAdapter;

    private SharedPreferences newsConfig;

    private static LoaderManager loaderManager;
    
    // Data binding blueprint/class of MainActivity
    private static ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainBinding.setClicker(new ClickHandler());

        // Create a new {@link NewsPopulator} that takes an empty, non-null {@link ArrayList} of
        // {@link News} as input.
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Get SharedPreferences link
        newsConfig = PreferenceManager.getDefaultSharedPreferences(this);

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

        // Define String values declared as instance variables using getString() method --
        // inaccessible outside context
        DEFAULT_OUTLET = getString(R.string.guardian_code);
        NEWS_OUTLET_PREFERNCE_KEY = getString(R.string.settings_news_outlet_key);
        PAGE_SIZE_PREFERENCE_KEY = getString(R.string.settings_page_size_key);

        // Get url from {@link SharedPreferences} and use it to generate appropriate {@link URL}
        String code = newsConfig.getString(NEWS_OUTLET_PREFERNCE_KEY, DEFAULT_OUTLET);
        Bundle seek = generateURL(code);

        // Check network state and start up {@link Loader}, passing generated {@link URL} if it's
        // connected, otherwise notify via {@link Snackbar}
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, seek,
                    (LoaderManager.LoaderCallbacks) MainActivity.this);
        } else {
            mMainBinding.pbNews.setVisibility(View.GONE);
            Snackbar.make(this, mMainBinding.frameSnack, "No net access!",
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

    /**
     * Register onPreferenceChangeListener and call generateURL() every time the
     * {@link AppCompatActivity} is resumed to make a request to the appropriate server.
     * Unregister the listener otherwise.
     */
    @Override
    protected void onPause () {
        super.onPause();
        newsConfig.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume () {
        super.onResume();
        newsConfig.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Use {@link Uri} & {@link Uri.Builder} to generate query {@link URL}.
     * @param apiCode retrieved from the {@link SharedPreferences} instance.
     * @return a bundle comprising the {@link URL} and discerned API code.
     */
    private Bundle generateURL (String apiCode) {
        Bundle seek = new Bundle();
        Uri base;
        Uri.Builder uriBuilder;

        final String NY_TIMES_CODE = getString(R.string.ny_times_code);
        final String NEWS_CODE = getString(R.string.news_code);
        final String GUARDIAN_CODE = getString(R.string.guardian_code);

        if (apiCode.equals(NY_TIMES_CODE)) {
            Log.i(LOG_TAG, "NY_TIMES api selected.");
            seek.putString("code", apiCode);

            base = Uri.parse(NY_TIMES_BASE_URL);
            uriBuilder = base.buildUpon();
            uriBuilder.appendPath(NY_TIMES_DEFAULT_PATH);
            uriBuilder.appendQueryParameter(getString(R.string.ny_times_page_size_query_param),
                    newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10"));

            // Attach apiCode & parsed New York Times API {@link URL} to bundle.
            seek.putString("link", "https://api.nytimes.com/svc/topstories/v2/" + NY_TimesSection
                    + ".json?api-key=" + NY_TIMES_API);
        } else if (apiCode.equals(NEWS_CODE)) {
            Log.i(LOG_TAG, "newsapi.org api selected.");

            base = Uri.parse(NEWS_API_BASE_URL);
            uriBuilder = base.buildUpon();
            uriBuilder.appendPath(NEWS_DEFAULT_PATH);
            // "Required parameters are missing. Please set any of the following parameters and
            // try again: sources, q, language, country, category."
            uriBuilder.appendQueryParameter("country", "ng");
            uriBuilder.appendQueryParameter(getString(R.string.news_page_size_query_param),
                    newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10"));
            uriBuilder.appendQueryParameter("apiKey", "6111dbc091194e9d9c5ba3d413d15971");

            // Attach apiCode & parsed newsapi.org API {@link URL} to bundle.
            seek.putString("code", apiCode);
            seek.putString("link", uriBuilder.toString());
        } else { // GUARDIAN_API_CODE:
            Log.i(LOG_TAG, "Default api chosen.");

            base = Uri.parse(GUARDIAN_API_BASE_URL);
            uriBuilder = base.buildUpon();
            uriBuilder.appendPath(GUARDIAN_DEFAULT_PATH);
            uriBuilder.appendQueryParameter(getString(R.string.guardian_page_size_query_param),
                    newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10"));
            uriBuilder.appendQueryParameter("api-key", "f8981f58-9f90-4bd8-91d7-c5f241f8e433");

            // Attach apiCode & parsed Default news API {@link URL} to bundle.
            seek.putString("code", apiCode);
            seek.putString("link", uriBuilder.toString());
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
                    data.get(0).getTotalArticles(),
                    data.get(0).getTotalArticles())
            );
            Log.i(LOG_TAG, "Number of articles(List): " + data.get(0).getTotalArticles());

            newsAdapter.addAll(data);
        } else {
            mMainBinding.tvNoa.setText(R.string.no_article_fetched);
            mMainBinding.tvNoa.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoaderReset() called");
        newsAdapter.clear();
    }

    /**
     * Restart {@link Loader} if the {@link SharedPreferences} key is recognised; show
     * {@link android.widget.ProgressBar} & hide the empty {@link View}
     * {@link android.widget.TextView}, and call #generateUrl(Bundle) to determine API to query,
     * then restart {@link Loader}. If case is otherwise, display a {@link Snackbar} notifying an
     * unknown {@link SharedPreferences}.
     * @param sharedPreferences received
     * @param key of the {@link SharedPreferences} changed
     */
    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        // Determine what {@link ListPreference} was modified, and restart loader to make new query.
        if(key.equals(NEWS_OUTLET_PREFERNCE_KEY) || key.equals(PAGE_SIZE_PREFERENCE_KEY)) {
            newsAdapter.clear();

            mMainBinding.pbNews.setVisibility(View.VISIBLE);
            mMainBinding.tvNoa.setVisibility(View.GONE);

            getSupportLoaderManager().restartLoader(LOADER_ID,
                    generateURL(newsConfig.getString(NEWS_OUTLET_PREFERNCE_KEY, DEFAULT_OUTLET)),
                    this);
        } else {
            Snackbar.make(this, (View) mMainBinding.frameSnack, "Unknown preference!",
                    Snackbar.LENGTH_LONG);
        }
    }


    public class ClickHandler implements View.OnClickListener {

        /**
         * Click hander for {@link News} outlet switch buttons.
         * Discerns what button was clicked, assigns correct api based on that, and calls
         * #onSharedPreferenceChanged() to restart the {@link Loader}, so new query could be made.
         * the @param v clicked
         */
        @Override
        public void onClick (View v) {
            int id = v.getId();
            String selectedAPI;

            if (id == mMainBinding.btnNewsApi.getId()) {
                selectedAPI = getString(R.string.news);
                newsConfig.edit().putString(NEWS_OUTLET_PREFERNCE_KEY, getString(R.string.news_code)).apply();
                onSharedPreferenceChanged(newsConfig, NEWS_OUTLET_PREFERNCE_KEY);
            } else if (id == mMainBinding.btnNyTimes.getId()) {
                selectedAPI = getString(R.string.ny_times);
                newsConfig.edit().putString(NEWS_OUTLET_PREFERNCE_KEY, getString(R.string.ny_times_code)).apply();
                onSharedPreferenceChanged(newsConfig, NEWS_OUTLET_PREFERNCE_KEY);
            } else {
                selectedAPI = getString(R.string.guardian);
                newsConfig.edit().putString(NEWS_OUTLET_PREFERNCE_KEY, getString(R.string.guardian_code)).apply();
                onSharedPreferenceChanged(newsConfig, NEWS_OUTLET_PREFERNCE_KEY);
            }

            Snackbar.make(MainActivity.this, (View) mMainBinding.frameSnack,
                    selectedAPI + " selected", Snackbar.LENGTH_LONG).show();
        }
    }
}