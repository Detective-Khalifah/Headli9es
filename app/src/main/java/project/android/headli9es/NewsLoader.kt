package project.android.headli9es

import android.content.Context
import android.os.Bundle
import androidx.loader.content.AsyncTaskLoader

class NewsLoader(context: Context?, lookupParams: Bundle) : AsyncTaskLoader<List<News>?>(
    context!!
) {
    private val apiCode: String?
    private val newsURL: String?
    private var result: List<News>? = null

    init {
        newsURL = lookupParams.getString("link")
        apiCode = lookupParams.getString("code")
    }

    override fun deliverResult(data: List<News>?) {
        result = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        super.onStartLoading()
        if (result != null) {
            deliverResult(result)
        } else forceLoad()
    }

    override fun loadInBackground(): List<News>? {

        // Don't perform the request if there are no URLs, or the first URL is null.
        return if (newsURL == null) {
            null
        } else {
            // Call static method #lookUpArticles, passing context passed when class was
            // instantiated by call to super(context), the {@link URL} & API code
            result = Search.lookupArticles(context, newsURL, apiCode)
            result
        }
    }
}