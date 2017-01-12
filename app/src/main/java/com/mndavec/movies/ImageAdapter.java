package com.mndavec.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.mndavec.movies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.List;

import static com.mndavec.movies.MainActivity.POSTERS_BASE_URL;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> movies;

    public ImageAdapter(Context c, List<Movie> movies) {
        context = c;
        this.movies = movies;
    }

    public int getCount() {
        return movies.size();
    }

    public Object getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return movies.get(position).id;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        Movie movie = movies.get(position);
        if (view == null) {
            view = new ImageView(context);
        }
        String url = POSTERS_BASE_URL + movie.poster_path;
        Picasso.with(context).load(url).into(view);
        view.setId(movie.id);
        return view;
    }
}