package com.mndavec.movies;

import com.google.auto.value.AutoValue;

@AutoValue
public class Movie {

    abstract String getMovieName();

    abstract String getPosterURL();

    static Builder builder() {
        return new AutoValue_Movie.Builder();
    }

    @AutoValue.Builder
    static abstract class Builder {

        abstract Builder setMovieName(String movieName);

        abstract Builder setPosterURL(String posterURL);

        abstract Movie build();
    }
}
