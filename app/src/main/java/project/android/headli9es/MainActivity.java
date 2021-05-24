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
import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LOADER_ID = 0;

    /** API Base {@link URL}s for the 3 {@link News} outlets. */
    private static final String GUARDIAN_API_BASE_URL = "https://content.guardianapis.com/";
    private static final String NY_TIMES_HOST = "https://api.nytimes.com";
    private static final String NY_TIMES_BASE_PATH = "/svc/topstories/v2";
    private static final String NEWS_API_BASE_URL = "https://newsapi.org/v2/";

    /** APIs' parameters & paths */
    private static final String GUARDIAN_DEFAULT_PATH = "search";
    private static final String NEWS_DEFAULT_PATH = "top-headlines";
    private static final String NY_TIMES_DEFAULT_SECTION = "home.json";

    /** Authorisation */
    private static final String GUARDIAN_AUTH = "f8981f58-9f90-4bd8-91d7-c5f241f8e433";
    private static final String GUARDIAN_AUTH_TAG = "api-key";
    private static final String NEWS_AUTH = "6111dbc091194e9d9c5ba3d413d15971";
    private static final String NEWS_AUTH_TAG = "apiKey";
    private static final String NY_TIMES_AUTH = "Vd6bJTsQALVX8fguWnFtpd37xZjch8f5";
    private static final String NY_TIMES_AUTH_TAG = "api-key";

    private static String DEFAULT_OUTLET, NEWS_OUTLET_PREFERENCE_KEY, PAGE_SIZE_PREFERENCE_KEY;

    private NewsAdapter newsAdapter;

    private SharedPreferences newsConfig;

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

        mMainBinding.linArticlesCount.tvNumArticles.setVisibility(GONE);
        mMainBinding.linArticlesCount.tvPageSize.setVisibility(GONE);

        // Define String values declared as instance variables using getString() method --
        // inaccessible outside context
        DEFAULT_OUTLET = getString(R.string.guardian_code);
        NEWS_OUTLET_PREFERENCE_KEY = getString(R.string.settings_news_outlet_key);
        PAGE_SIZE_PREFERENCE_KEY = getString(R.string.settings_page_size_key);

        // Get url from {@link SharedPreferences} and use it to generate appropriate {@link URL}
        String code = newsConfig.getString(NEWS_OUTLET_PREFERENCE_KEY, DEFAULT_OUTLET);
        Bundle seek = generateURL(code);

        // Check network state and start up {@link Loader}, passing generated {@link URL} if it's
        // connected, otherwise notify via {@link Snackbar}
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, seek,
                    (LoaderManager.LoaderCallbacks) MainActivity.this);
        } else {
            mMainBinding.pbNews.setVisibility(GONE);
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

    @Override
    public Loader<List<News>> onCreateLoader (int i, final Bundle bundle) {
        return new NewsLoader(this, bundle);
    }

    @Override
    public void onLoadFinished (Loader<List<News>> loader, List<News> data) {
        mMainBinding.pbNews.setVisibility(GONE);
        mMainBinding.linArticlesCount.tvNumArticles.setVisibility(View.VISIBLE);

        newsAdapter.notifyDataSetChanged();

        // If there is a valid list of {@link News}, then add them to the {@link NewsAdapter}'s dataset.
        // This will trigger the {@link ListView} to update.
        if (data != null && !data.isEmpty()) {

            mMainBinding.tvNoa.setVisibility(View.VISIBLE);
            mMainBinding.linArticlesCount.tvNumArticles.setText(getResources().getQuantityString(
                    R.plurals.articles_count,
                    data.get(0).getTotalArticles(),
                    data.get(0).getTotalArticles())
            );
            mMainBinding.linArticlesCount.tvPageSize.setText(getResources().getQuantityString(
                    R.plurals.news_page_size,
                    data.get(0).getPageSize(),
                    data.get(0).getPageSize()
            ));
            if (data.get(0).getPageSize() < 1)
                mMainBinding.linArticlesCount.tvPageSize.setVisibility(GONE);
            else
                mMainBinding.linArticlesCount.tvPageSize.setVisibility(View.VISIBLE);

            newsAdapter.addAll(data);
        } else {
            mMainBinding.tvNoa.setText(R.string.no_article_fetched);
            mMainBinding.tvNoa.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset (androidx.loader.content.Loader<List<News>> loader) {
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
        if(key.equals(NEWS_OUTLET_PREFERENCE_KEY) || key.equals(PAGE_SIZE_PREFERENCE_KEY)) {
            newsAdapter.clear();

            mMainBinding.pbNews.setVisibility(View.VISIBLE);
            mMainBinding.linArticlesCount.tvNumArticles.setVisibility(GONE);
            mMainBinding.linArticlesCount.tvPageSize.setVisibility(GONE);
            mMainBinding.tvNoa.setVisibility(GONE);

            getSupportLoaderManager().restartLoader(LOADER_ID,
                    generateURL(newsConfig.getString(NEWS_OUTLET_PREFERENCE_KEY, DEFAULT_OUTLET)),
                    this);
        } else {
            Snackbar.make(this, (View) mMainBinding.frameSnack, "Unknown preference!",
                    Snackbar.LENGTH_LONG);
        }
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

        if (apiCode.equals(NY_TIMES_CODE)) {

            base = Uri.parse(NY_TIMES_HOST);
            uriBuilder = base.buildUpon();
            uriBuilder.appendEncodedPath(NY_TIMES_BASE_PATH);
            uriBuilder.appendPath(NY_TIMES_DEFAULT_SECTION);
            uriBuilder.appendQueryParameter(NY_TIMES_AUTH_TAG, NY_TIMES_AUTH);

            // Attach apiCode & parsed New York Times API {@link URL} to bundle.
            seek.putString("code", apiCode);
            seek.putString("link", uriBuilder.toString());
        } else if (apiCode.equals(NEWS_CODE)) {

            base = Uri.parse(NEWS_API_BASE_URL);
            uriBuilder = base.buildUpon();
            uriBuilder.appendPath(NEWS_DEFAULT_PATH);
            // "Required parameters are missing. Please set any of the following parameters and
            // try again: sources, q, language, country, category."
            uriBuilder.appendQueryParameter("country", "ng");
            uriBuilder.appendQueryParameter(getString(R.string.news_page_size_query_param),
                    newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10"));
            uriBuilder.appendQueryParameter(NEWS_AUTH_TAG, NEWS_AUTH);

            // Attach apiCode & parsed newsapi.org API {@link URL} to bundle.
            seek.putString("code", apiCode);
            seek.putString("link", uriBuilder.toString());
        } else { // GUARDIAN_API_CODE:

            base = Uri.parse(GUARDIAN_API_BASE_URL);
            uriBuilder = base.buildUpon();
            uriBuilder.appendPath(GUARDIAN_DEFAULT_PATH);
            uriBuilder.appendQueryParameter(getString(R.string.guardian_page_size_query_param),
                    newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10"));
            uriBuilder.appendQueryParameter("show-tags", "contributor");
            uriBuilder.appendQueryParameter(GUARDIAN_AUTH_TAG, GUARDIAN_AUTH);

            Log.i(MainActivity.class.getName(), "url:: " + uriBuilder);
            // Attach apiCode & parsed Default news API {@link URL} to bundle.
            seek.putString("code", apiCode);
            seek.putString("link", uriBuilder.toString());
        }
        disableSelectedButton(apiCode);
        return seek;
    }


    /**
     * Disable the selected outlet's button from getting re-clicked, to avoid data waste when it's
     * already being clicked, and it's API is being consumed.
     * @param API of the outlet button clicked.
     */
    private void disableSelectedButton (String API) {
        if (API.equals(getString(R.string.ny_times_code))) {
            mMainBinding.btnNyTimes.setClickable(false);

            mMainBinding.btnGuardian.setClickable(true);
            mMainBinding.btnNewsApi.setClickable(true);
        } else if (API.equals(getString(R.string.news_code))) {
            mMainBinding.btnNewsApi.setClickable(false);

            mMainBinding.btnNyTimes.setClickable(true);
            mMainBinding.btnGuardian.setClickable(true);
        } else {
            mMainBinding.btnGuardian.setClickable(false);

            mMainBinding.btnNewsApi.setClickable(true);
            mMainBinding.btnNyTimes.setClickable(true);
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
                newsConfig.edit().putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.news_code)).apply();
            } else if (id == mMainBinding.btnNyTimes.getId()) {
                selectedAPI = getString(R.string.ny_times);
                newsConfig.edit().putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.ny_times_code)).apply();
            } else {
                selectedAPI = getString(R.string.guardian);
                newsConfig.edit().putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.guardian_code)).apply();
            }

            onSharedPreferenceChanged(newsConfig, NEWS_OUTLET_PREFERENCE_KEY);
            Snackbar.make(MainActivity.this, (View) mMainBinding.frameSnack,
                    selectedAPI + " selected", Snackbar.LENGTH_LONG).show();
        }

    }
}