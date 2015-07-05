package com.example.android.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableArtist implements Parcelable {
    public String id;
    public String name;
    public String poster;

    @Override
    public int describeContents() {
        return 0;
    }

    public ParcelableArtist() {
        super();
    }

    public ParcelableArtist(Parcel source) {
        id = source.readString();
        name = source.readString();
        poster = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(poster);
    }

    public static final Parcelable.Creator<ParcelableArtist> CREATOR
            = new Parcelable.Creator<ParcelableArtist>() {
        public ParcelableArtist createFromParcel(Parcel in) {
            return new ParcelableArtist(in);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };
}
