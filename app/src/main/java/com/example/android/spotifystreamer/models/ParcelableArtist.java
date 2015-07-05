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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(poster);
    }
}
