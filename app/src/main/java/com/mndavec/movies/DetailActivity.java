package com.mndavec.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.mndavec.movies.model.Movie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.mndavec.movies.MainActivity.MOVIE_ID;

public class DetailActivity extends AppCompatActivity {

    final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long movieID = intent.getLongExtra(MOVIE_ID, 0);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadMovie(movieID);
    }

    private void loadMovie(long movieID) {
        String movieStrID = Long.toString(movieID);
        FetchMovieDetail getMovieTask = new FetchMovieDetail();
        getMovieTask.execute(movieStrID);
    }

    public class FetchMovieDetail extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = MainActivity.FetchMovieList.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                final String APIKEY_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                final String LANG_ENGLISH = "en-US";
                final String SORT_PARAM = "sort_by";
                final String POPULAR_SORT = "popularity.desc";
                final String ADULT_PARAM = "include_adult";
                final String MOVIE_ID_PARAM = "movie_id";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, LANG_ENGLISH)
                        //.appendQueryParameter(SORT_PARAM, POPULAR_SORT)
                        //.appendQueryParameter(ADULT_PARAM, "false")
                        //                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                moviesJsonStr = buffer.toString();
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

            //try {
            //    return getMovieDataFromJson(moviesJsonStr);
            //} catch (JSONException e) {
            //    Log.e(LOG_TAG, e.getMessage(), e);
            //    e.printStackTrace();
            //}
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            //moviePosterAdapter = new ImageAdapter(context, result);
            //GridView gridview = (GridView) findViewById(R.id.grid_posters);
            //gridview.setAdapter(moviePosterAdapter);
            //gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long itemID) {
            //        Intent myIntent = new Intent(getApplicationContext(),DetailActivity.class);
            //        myIntent.putExtra(MOVIE_ID, itemID);
            //        startActivity(myIntent);
            //    }
            //});
            //Log.d(this.toString(), "loaded posters");
        }
    }

}
