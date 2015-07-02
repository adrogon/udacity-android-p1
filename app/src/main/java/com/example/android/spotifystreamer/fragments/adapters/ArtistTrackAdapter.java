package com.example.android.spotifystreamer.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.ParcelableTrack;
import com.example.android.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistTrackAdapter extends ArrayAdapter<ParcelableTrack> {
    private Context context;
    private List<ParcelableTrack> parcelableTracks;

    public ArtistTrackAdapter(Context context, List<ParcelableTrack> parcelableTracks) {
        super(context, -1, parcelableTracks);
        this.context = context;
        this.parcelableTracks = parcelableTracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.artist_track_list_item, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.artist_track_list_item_poster);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.artist_track_list_item_title);
        TextView albumTextView = (TextView) rowView.findViewById(R.id.artist_track_list_item_album);

        ParcelableTrack parcelableTrack = parcelableTracks.get(position);

        titleTextView.setText(parcelableTrack.title);
        albumTextView.setText(parcelableTrack.album);

        if (parcelableTrack.poster == null) {
            imageView.setImageResource(R.drawable.no_image);
        } else {
            Picasso.with(context).load(parcelableTrack.poster).into(imageView);
        }

        return rowView;
    }
}
