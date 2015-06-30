package com.example.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTrack implements Parcelable {
    String id;
    String title;
    String album;
    String poster;
    String previewUrl;

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
