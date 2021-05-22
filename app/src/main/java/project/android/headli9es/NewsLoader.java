package project.android.headli9es;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import androidx.loader.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private String apiCode, newsURL;
    private List<News> result;

    public NewsLoader (Context context, Bundle lookupParams) {
        super(context);
        newsURL = lookupParams.getString("link");
        apiCode = lookupParams.getString("code");
    }

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
        Log.i(LOG_TAG, "This is loadInBackground. I received: " + newsURL);

        // Don't perform the request if there are no URLs, or the first URL is null.
        if (newsURL == null) {
            Log.i(this.getClass().getName(), "Conditional check finds null");
            return null;
        } else {
            result = Search.lookUpArticles(newsURL);
            Log.i(this.getClass().getName(), "result List data: " + result);
            return result;
        }
    }
}