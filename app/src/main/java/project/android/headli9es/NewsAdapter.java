package project.android.headli9es;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import project.android.headli9es.databinding.ForecastBinding;

import static androidx.core.content.ContextCompat.startActivity;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();
    private LayoutInflater inflater;

    protected ForecastBinding binder;

    public NewsAdapter (Context context, List<News> objects) {
        super(context, 0, objects);
        Log.d(LOG_TAG, "This is NewsPopulator.");
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = ( (Activity) parent.getContext()).getLayoutInflater();

        News currentArticle = getItem(position);

        if (binder == null)
            binder = DataBindingUtil.inflate(inflater, R.layout.forecast, parent, false);

        binder.setNews(currentArticle);
        binder.executePendingBindings();

//        return convertView;
        return binder.getRoot();
    }
}