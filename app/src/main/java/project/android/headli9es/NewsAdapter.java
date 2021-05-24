package project.android.headli9es;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import project.android.headli9es.databinding.ArticleBinding;

public class NewsAdapter extends ArrayAdapter<News> {

    private LayoutInflater inflater;
    private Context mAppContext;

    protected ArticleBinding binder;

    public NewsAdapter (Context context, List<News> objects) {
        super(context, 0, objects);
        mAppContext = context;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = ( (Activity) mAppContext).getLayoutInflater();

        News currentArticle = getItem(position);

        binder = DataBindingUtil.getBinding(convertView);
        if (binder == null)
            binder = DataBindingUtil.inflate(inflater, R.layout.article, parent, false);

        binder.setNews(currentArticle);
        binder.executePendingBindings();

        return binder.getRoot();
    }
}