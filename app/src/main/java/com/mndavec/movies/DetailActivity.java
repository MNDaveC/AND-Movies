package com.mndavec.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mndavec.movies.model.MovieDetail;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;

import static com.mndavec.movies.MainActivity.MOVIE_ID;
import static com.mndavec.movies.MainActivity.MOVIE_TITLE;
import static com.mndavec.movies.Utils.convertDpToPixel;

public class DetailActivity extends AppCompatActivity {

    final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String WIDTH_PARAM = "w";
    final static int DESIRED_WIDTH_DP = 400;
    public static final String POSTERS_BASE_URL2 = "https://image.tmdb.org/t/p";


    Context context = (Context) this;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long movieID = intent.getLongExtra(MOVIE_ID, 0);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(intent.getStringExtra(MOVIE_TITLE));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadMovie(movieID);

    }

    private void loadMovie(long movieID) {
        String movieStrID = Long.toString(movieID);
        FetchMovieDetail getMovieTask = new FetchMovieDetail();
        getMovieTask.execute(movieStrID);
    }

    public class FetchMovieDetail extends AsyncTask<String, Void, MovieDetail> {
        private final String LOG_TAG = MainActivity.FetchMovieList.class.getSimpleName();

        @Override
        protected MovieDetail doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieDetailJsonStr = null;

            try {

                final String APIKEY_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                final String LANG_ENGLISH = "en-US";


                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, LANG_ENGLISH)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDetailJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDetailFromJson(movieDetailJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private MovieDetail getMovieDetailFromJson (String jsonDetail) throws JSONException {
            MovieDetail movieDetail =  new Gson().fromJson(jsonDetail, MovieDetail.class);
            return movieDetail;
        }

        @Override
        protected void onPostExecute(MovieDetail result) {

            setTitle(result.original_title);

            TextView synopsis = (TextView) findViewById(R.id.synopsis);
            synopsis.setText(result.overview);

            float starRating = new Float(result.vote_average);
            starRating = starRating /2.0f;
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating(starRating);

            TextView textRating = (TextView) findViewById(R.id.rating_text);
            textRating.setText("(" + result.vote_average + " / 10)");

            int widthPx = (int) convertDpToPixel(DESIRED_WIDTH_DP, context);
            ImageView imageView = (ImageView) findViewById(R.id.movie_detail_poster);
            Uri posterUri = Uri.parse(POSTERS_BASE_URL2).buildUpon()
                    .appendPath(WIDTH_PARAM + Integer.toString(widthPx))
                    .appendPath(result.poster_path.replace("/", ""))
                    .build();

            Picasso.with(context).load(posterUri).into(imageView);

            Log.d(this.toString(), "loaded posters");
        }
    }


}
