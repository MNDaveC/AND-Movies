package com.mndavec.movies;

import com.google.gson.annotations.SerializedName;
import com.mndavec.movies.model.Movie;
import java.util.List;

public class WrappedMovieResponse {
    @SerializedName("page")
    private final int pageNumber;

    @SerializedName("results")
    public final List<Movie> wrappedMovies;

    public WrappedMovieResponse(int pageNumber, List<Movie> wrappedMovies) {
        this.pageNumber = pageNumber;
        this.wrappedMovies = wrappedMovies;
    }
}
