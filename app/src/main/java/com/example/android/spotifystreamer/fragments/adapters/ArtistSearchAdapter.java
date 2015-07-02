package com.example.android.spotifystreamer.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistSearchAdapter extends ArrayAdapter<Artist> {
    private Context context;
    private List<Artist> artists;

    public ArtistSearchAdapter(Context context, List<Artist> artists) {
        super(context, -1, artists);
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.artist_search_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.artist_search_list_item_name);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.artist_search_list_item_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Artist artist = artists.get(position);
        if (artist != null) {
            viewHolder.textView.setText(artist.name);

            // No instruction here, so just keep the first image if any
            if (artist.images == null || artist.images.isEmpty() || artist.images.get(0) == null
                    || artist.images.get(0).url == null || artist.images.get(0).url.isEmpty()) {
                viewHolder.imageView.setImageResource(R.drawable.no_image);
            } else {
                Picasso.with(context).load(artist.images.get(0).url).into(viewHolder.imageView);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
