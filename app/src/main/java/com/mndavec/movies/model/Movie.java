package com.mndavec.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public int id;

    public String original_title;

    public String poster_path;

    public String overview;

    public String release_date;

    public String vote_average;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.original_title);
        dest.writeString(this.poster_path);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeString(this.vote_average);
    }

    public Movie() {}

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.original_title = in.readString();
        this.poster_path = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.vote_average = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override public Movie createFromParcel(Parcel source) {return new Movie(source);}

        @Override public Movie[] newArray(int size) {return new Movie[size];}
    };
}
