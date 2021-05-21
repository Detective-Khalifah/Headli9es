package project.android.headli9es;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import project.android.headli9es.databinding.ForecastBinding;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();
    private LayoutInflater inflater;
    private Context mAppContext;

    protected ForecastBinding binder;

    public NewsAdapter (Context context, List<News> objects) {
        super(context, 0, objects);
        Log.d(LOG_TAG, "This is NewsPopulator.");
        mAppContext = context;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = ( (Activity) mAppContext).getLayoutInflater();

        News currentArticle = getItem(position);

        binder = DataBindingUtil.getBinding(convertView);
        if (binder == null)
            binder = DataBindingUtil.inflate(inflater, R.layout.forecast, parent, false);


        binder.setNews(currentArticle);
        binder.executePendingBindings();

        return binder.getRoot();
    }
}