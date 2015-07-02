package com.example.android.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTrack implements Parcelable {
    public String id;
    public String title;
    public String album;
    public String poster;
    public String previewUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(poster);
        dest.writeString(previewUrl);
    }
}
