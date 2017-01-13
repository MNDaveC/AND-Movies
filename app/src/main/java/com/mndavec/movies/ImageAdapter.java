package com.mndavec.movies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.mndavec.movies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.List;

import static com.mndavec.movies.DetailActivity.POSTERS_BASE_URL2;
import static com.mndavec.movies.DetailActivity.WIDTH_PARAM;
import static com.mndavec.movies.Utils.convertDpToPixel;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> movies;
    final static int DESIRED_WIDTH_DP = 200;


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

        int widthPx = (int) convertDpToPixel(DESIRED_WIDTH_DP, context);
        Uri posterUri = Uri.parse(POSTERS_BASE_URL2).buildUpon()
                .appendPath(WIDTH_PARAM + Integer.toString(widthPx))
                .appendPath(movie.poster_path.replace("/", ""))
                .build();

        //String url = POSTERS_BASE_URL + movie.poster_path;
        Picasso.with(context).load(posterUri).into(view);
        view.setId(movie.id);
        return view;
    }
}