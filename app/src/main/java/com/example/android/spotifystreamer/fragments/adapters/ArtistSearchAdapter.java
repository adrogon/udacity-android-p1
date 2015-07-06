package com.example.android.spotifystreamer.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.models.ParcelableArtist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistSearchAdapter extends ArrayAdapter<ParcelableArtist> {
    private Context context;
    private List<ParcelableArtist> parcelableArtists;

    public ArtistSearchAdapter(Context context, List<ParcelableArtist> parcelableArtists) {
        super(context, -1, parcelableArtists);
        this.context = context;
        this.parcelableArtists = parcelableArtists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {

                convertView = inflater.inflate(R.layout.artist_search_list_item, parent, false);

                if (convertView != null) {
                    viewHolder = new ViewHolder();

                    viewHolder.textView = (TextView) convertView.findViewById(R.id.artist_search_list_item_name);
                    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.artist_search_list_item_poster);

                    convertView.setTag(viewHolder);
                }
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null && viewHolder.textView != null && viewHolder.imageView != null) {

            ParcelableArtist parcelableArtist = parcelableArtists.get(position);

            if (parcelableArtist != null) {

                viewHolder.textView.setText(parcelableArtist.name);

                if (parcelableArtist.poster == null || parcelableArtist.poster.isEmpty()) {
                    viewHolder.imageView.setImageResource(R.drawable.no_image);
                } else {
                    Picasso.with(context).load(parcelableArtist.poster).into(viewHolder.imageView);
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
