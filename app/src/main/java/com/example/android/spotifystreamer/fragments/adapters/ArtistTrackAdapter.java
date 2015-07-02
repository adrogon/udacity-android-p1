package com.example.android.spotifystreamer.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.models.ParcelableTrack;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.artist_track_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.artist_track_list_item_title);
            viewHolder.albumTextView = (TextView) convertView.findViewById(R.id.artist_track_list_item_album);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.artist_track_list_item_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParcelableTrack parcelableTrack = parcelableTracks.get(position);
        if (parcelableTrack != null) {
            viewHolder.titleTextView.setText(parcelableTrack.title);
            viewHolder.albumTextView.setText(parcelableTrack.album);

            if (parcelableTrack.poster == null || parcelableTrack.poster.isEmpty()) {
                viewHolder.imageView.setImageResource(R.drawable.no_image);
            } else {
                Picasso.with(context).load(parcelableTrack.poster).into(viewHolder.imageView);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView titleTextView;
        TextView albumTextView;
        ImageView imageView;
    }
}
