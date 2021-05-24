package project.android.headli9es;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import androidx.loader.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

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

        // Don't perform the request if there are no URLs, or the first URL is null.
        if (newsURL == null) {
            return null;
        } else {
            // Call static method #lookUpArticles, passing context passed when class was
            // instantiated by call to super(context), the {@link URL} & API code
            result = Search.lookupArticles(getContext(), newsURL, apiCode);
            return result;
        }
    }
}