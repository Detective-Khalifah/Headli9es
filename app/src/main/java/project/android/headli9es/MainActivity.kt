package project.android.headli9es

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.google.android.material.snackbar.Snackbar
import project.android.headli9es.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<News>?>,
    OnSharedPreferenceChangeListener {
    private var newsAdapter: NewsAdapter? = null
    private lateinit var newsConfig: SharedPreferences

    // Data binding blueprint/class of MainActivity
    private lateinit var mMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mMainBinding.clicker = ClickHandler()

        // Create a new {@link NewsPopulator} that takes an empty, non-null {@link ArrayList} of
        // {@link News} as input.
        newsAdapter = NewsAdapter(this, ArrayList())

        // Get SharedPreferences link
        newsConfig = PreferenceManager.getDefaultSharedPreferences(this)
        mMainBinding.listView.adapter = newsAdapter
        mMainBinding.listView.emptyView = mMainBinding.tvNoa
        mMainBinding.listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(newsAdapter!!.getItem(position)!!.page)
                    )
                )
            }
        mMainBinding.linArticlesCount.tvNumArticles.visibility = View.GONE
        mMainBinding.linArticlesCount.tvPageSize.visibility = View.GONE

        // Define String values declared as instance variables using getString() method --
        // inaccessible outside context
        DEFAULT_OUTLET = getString(R.string.guardian_code)
        NEWS_OUTLET_PREFERENCE_KEY = getString(R.string.settings_news_outlet_key)
        PAGE_SIZE_PREFERENCE_KEY = getString(R.string.settings_page_size_key)

        // Get url from {@link SharedPreferences} and use it to generate appropriate {@link URL}
        val code = newsConfig.getString(NEWS_OUTLET_PREFERENCE_KEY, DEFAULT_OUTLET)
        val seek = generateURL(code)

        // Check network state and start up {@link Loader}, passing generated {@link URL} if it's
        // connected, otherwise notify via {@link Snackbar}
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connManager.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            val loaderManager = supportLoaderManager
            loaderManager.initLoader(
                LOADER_ID, seek,
                this@MainActivity as LoaderManager.LoaderCallbacks<List<News>>
            )
        } else {
            mMainBinding.pbNews.visibility = View.GONE
            Snackbar.make(
                this, mMainBinding.frameSnack, "No net access!",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.api_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.news_settings_menu) {
            startActivity(Intent(this, ConfigActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Register onPreferenceChangeListener and call generateURL() every time the
     * [AppCompatActivity] is resumed to make a request to the appropriate server.
     * Unregister the listener otherwise.
     */
    override fun onPause() {
        super.onPause()
        newsConfig.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        newsConfig.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<List<News>?> {
        return NewsLoader(this, bundle)
    }

    override fun onLoadFinished(loader: Loader<List<News>?>, data: List<News>?) {
        mMainBinding.pbNews.visibility = View.GONE
        mMainBinding.linArticlesCount.tvNumArticles.visibility = View.VISIBLE
        newsAdapter!!.clear()

        // If there is a valid list of {@link News}, then add them to the {@link NewsAdapter}'s dataset.
        // This will trigger the {@link ListView} to update.
        if (data != null && !data.isEmpty()) {
            mMainBinding.tvNoa.visibility = View.VISIBLE
            mMainBinding.linArticlesCount.tvNumArticles.text = resources.getQuantityString(
                R.plurals.articles_count,
                data[0].totalArticles,
                data[0].totalArticles
            )
            mMainBinding.linArticlesCount.tvPageSize.text = resources.getQuantityString(
                R.plurals.news_page_size,
                data[0].pageSize,
                data[0].pageSize
            )
            if (data[0].pageSize < 1) mMainBinding.linArticlesCount.tvPageSize.visibility =
                View.GONE else mMainBinding.linArticlesCount.tvPageSize.visibility = View.VISIBLE
            newsAdapter!!.addAll(data)
        } else {
            mMainBinding.tvNoa.setText(R.string.no_article_fetched)
            mMainBinding.tvNoa.visibility = View.VISIBLE
        }
    }

    override fun onLoaderReset(loader: Loader<List<News>?>) {
        newsAdapter!!.clear()
    }

    /**
     * Restart [Loader] if the [SharedPreferences] key is recognised; show
     * [android.widget.ProgressBar] & hide the empty [View]
     * [android.widget.TextView], and call #generateUrl(Bundle) to determine API to query,
     * then restart [Loader]. If case is otherwise, display a [Snackbar] notifying an
     * unknown [SharedPreferences].
     * @param sharedPreferences received
     * @param key of the [SharedPreferences] changed
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Determine what {@link ListPreference} was modified, and restart loader to make new query.
        if (key == NEWS_OUTLET_PREFERENCE_KEY || key == PAGE_SIZE_PREFERENCE_KEY) {
            newsAdapter!!.clear()
            mMainBinding.pbNews.visibility = View.VISIBLE
            mMainBinding.linArticlesCount.tvNumArticles.visibility = View.GONE
            mMainBinding.linArticlesCount.tvPageSize.visibility = View.GONE
            mMainBinding.tvNoa.visibility = View.GONE
            supportLoaderManager.restartLoader(
                LOADER_ID,
                generateURL(newsConfig.getString(NEWS_OUTLET_PREFERENCE_KEY, DEFAULT_OUTLET)),
                this
            )
        } else {
            Snackbar.make(
                this, (mMainBinding.frameSnack as View), "Unknown preference!",
                Snackbar.LENGTH_LONG
            )
        }
    }

    /**
     * Use [Uri] & [Uri.Builder] to generate query [URL].
     * @param apiCode retrieved from the [SharedPreferences] instance.
     * @return a bundle comprising the [URL] and discerned API code.
     */
    private fun generateURL(apiCode: String?): Bundle {
        val seek = Bundle()
        val base: Uri
        val uriBuilder: Uri.Builder
        val NY_TIMES_CODE = getString(R.string.ny_times_code)
        val NEWS_CODE = getString(R.string.news_code)
        if (apiCode == NY_TIMES_CODE) {
            base = Uri.parse(NY_TIMES_HOST)
            uriBuilder = base.buildUpon()
            uriBuilder.appendEncodedPath(NY_TIMES_BASE_PATH)
            uriBuilder.appendPath(NY_TIMES_DEFAULT_SECTION)
            uriBuilder.appendQueryParameter(NY_TIMES_AUTH_TAG, NY_TIMES_AUTH)

            // Attach apiCode & parsed New York Times API {@link URL} to bundle.
            seek.putString("code", apiCode)
            seek.putString("link", uriBuilder.toString())
        } else if (apiCode == NEWS_CODE) {
            base = Uri.parse(NEWS_API_BASE_URL)
            uriBuilder = base.buildUpon()
            uriBuilder.appendPath(NEWS_DEFAULT_PATH)
            // "Required parameters are missing. Please set any of the following parameters and
            // try again: sources, q, language, country, category."
            uriBuilder.appendQueryParameter("country", "ng")
            uriBuilder.appendQueryParameter(
                getString(R.string.news_page_size_query_param),
                newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10")
            )
            uriBuilder.appendQueryParameter(NEWS_AUTH_TAG, NEWS_AUTH)

            // Attach apiCode & parsed newsapi.org API {@link URL} to bundle.
            seek.putString("code", apiCode)
            seek.putString("link", uriBuilder.toString())
        } else { // GUARDIAN_API_CODE:
            base = Uri.parse(GUARDIAN_API_BASE_URL)
            uriBuilder = base.buildUpon()
            uriBuilder.appendPath(GUARDIAN_DEFAULT_PATH)
            uriBuilder.appendQueryParameter(
                getString(R.string.guardian_page_size_query_param),
                newsConfig.getString(PAGE_SIZE_PREFERENCE_KEY, "10")
            )
            uriBuilder.appendQueryParameter("show-tags", "contributor")
            uriBuilder.appendQueryParameter(GUARDIAN_AUTH_TAG, GUARDIAN_AUTH)
            Log.i(MainActivity::class.java.name, "url:: $uriBuilder")
            // Attach apiCode & parsed Default news API {@link URL} to bundle.
            seek.putString("code", apiCode)
            seek.putString("link", uriBuilder.toString())
        }
        disableSelectedButton(apiCode)
        return seek
    }

    /**
     * Disable the selected outlet's button from getting re-clicked, to avoid data waste when it's
     * already being clicked, and it's API is being consumed.
     * @param API of the outlet button clicked.
     */
    private fun disableSelectedButton(API: String?) {
        if (API == getString(R.string.ny_times_code)) {
            mMainBinding.btnNyTimes.isClickable = false
            mMainBinding.btnGuardian.isClickable = true
            mMainBinding.btnNewsApi.isClickable = true
        } else if (API == getString(R.string.news_code)) {
            mMainBinding.btnNewsApi.isClickable = false
            mMainBinding.btnNyTimes.isClickable = true
            mMainBinding.btnGuardian.isClickable = true
        } else {
            mMainBinding.btnGuardian.isClickable = false
            mMainBinding.btnNewsApi.isClickable = true
            mMainBinding.btnNyTimes.isClickable = true
        }
    }

    inner class ClickHandler : View.OnClickListener {
        /**
         * Click hander for [News] outlet switch buttons.
         * Discerns what button was clicked, assigns correct api based on that, and calls
         * #onSharedPreferenceChanged() to restart the [Loader], so new query could be made.
         * the @param v clicked
         */
        override fun onClick(v: View) {
            val id = v.id
            val selectedAPI: String
            if (id == mMainBinding.btnNewsApi.id) {
                selectedAPI = getString(R.string.news)
                newsConfig.edit()
                    .putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.news_code)).apply()
            } else if (id == mMainBinding.btnNyTimes.id) {
                selectedAPI = getString(R.string.ny_times)
                newsConfig.edit()
                    .putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.ny_times_code))
                    .apply()
            } else {
                selectedAPI = getString(R.string.guardian)
                newsConfig.edit()
                    .putString(NEWS_OUTLET_PREFERENCE_KEY, getString(R.string.guardian_code))
                    .apply()
            }
            onSharedPreferenceChanged(newsConfig, NEWS_OUTLET_PREFERENCE_KEY!!)
            Snackbar.make(
                this@MainActivity, (mMainBinding.frameSnack as View),
                "$selectedAPI selected", Snackbar.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        private const val LOADER_ID = 0

        /** API Base [URL]s for the 3 [News] outlets.  */
        private const val GUARDIAN_API_BASE_URL = "https://content.guardianapis.com/"
        private const val NY_TIMES_HOST = "https://api.nytimes.com"
        private const val NY_TIMES_BASE_PATH = "/svc/topstories/v2"
        private const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"

        /** APIs' parameters & paths  */
        private const val GUARDIAN_DEFAULT_PATH = "search"
        private const val NEWS_DEFAULT_PATH = "top-headlines"
        private const val NY_TIMES_DEFAULT_SECTION = "home.json"

        /** Authorisation  */
        private const val GUARDIAN_AUTH = "f8981f58-9f90-4bd8-91d7-c5f241f8e433"
        private const val GUARDIAN_AUTH_TAG = "api-key"
        private const val NEWS_AUTH = "6111dbc091194e9d9c5ba3d413d15971"
        private const val NEWS_AUTH_TAG = "apiKey"
        private const val NY_TIMES_AUTH = "Vd6bJTsQALVX8fguWnFtpd37xZjch8f5"
        private const val NY_TIMES_AUTH_TAG = "api-key"
        private var DEFAULT_OUTLET: String? = null
        private var NEWS_OUTLET_PREFERENCE_KEY: String? = null
        private var PAGE_SIZE_PREFERENCE_KEY: String? = null
    }
}