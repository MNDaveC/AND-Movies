package com.mndavec.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mndavec.movies.model.Movie;
import com.squareup.picasso.Picasso;

import static com.mndavec.movies.MainActivity.MOVIE_INFO;
import static com.mndavec.movies.Utils.convertDpToPixel;

public class DetailActivity extends AppCompatActivity {

    final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String WIDTH_PARAM = "w";
    final static int DESIRED_WIDTH_DP = 400;
    public static final String POSTERS_BASE_URL2 = "https://image.tmdb.org/t/p";


    @BindView(R.id.synopsis) TextView synopsis;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.rating_text) TextView textRating;
    @BindView(R.id.movie_detail_poster) ImageView imageView;
    @BindView(R.id.release_date) TextView releaseDate;
    Context context = (Context) this;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getParcelableExtra(MOVIE_INFO);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        displayMovie(movie);

    }

    private void displayMovie(Movie movie) {
        setTitle(movie.original_title);
        synopsis.setText(movie.overview);
        float starRating = new Float(movie.vote_average);
        starRating = starRating /2.0f;
        ratingBar.setRating(starRating);
        textRating.setText("(" + movie.vote_average + " / 10)");
        releaseDate.setText(movie.release_date);

        int widthPx = (int) convertDpToPixel(DESIRED_WIDTH_DP, context);
        Uri posterUri = Uri.parse(POSTERS_BASE_URL2).buildUpon()
                .appendPath(WIDTH_PARAM + Integer.toString(widthPx))
                .appendPath(movie.poster_path.replace("/", ""))
                .build();

        Picasso.with(context).load(posterUri).into(imageView);

    }
}
