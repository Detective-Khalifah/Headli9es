package project.android.headli9es;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;


class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String query;

    protected NewsLoader (Context context, String theQuery) {
        super(context);
        this.query = theQuery;
    }

    @Override
    protected void onStartLoading () {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<News> loadInBackground () {
        Log.i(NewsLoader.class.getName(), "This is loadInBackground. I received: " + query);
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (query.length() < 1) {
//            Log.i(BookLoader.class.getName(), "Conditional check finds null");
            return null;
        } else {
            List<News> result = Search.lookUpVolumes(query);
            Log.i(NewsLoader.class.getName(), "result List data: " + result);
            return result;
        }
    }
}