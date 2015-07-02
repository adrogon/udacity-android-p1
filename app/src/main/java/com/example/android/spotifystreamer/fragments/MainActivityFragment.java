package com.example.android.spotifystreamer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.activities.ArtistTracksActivity;
import com.example.android.spotifystreamer.fragments.adapters.ArtistSearchAdapter;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivityFragment extends Fragment {
    private String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // Minimum characters to perform a search
    private final static int SEARCH_MIN_CHARS = 3;

    private ArtistSearchAdapter artistSearchAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistSearchAdapter = new ArtistSearchAdapter(getActivity().getBaseContext(), new ArrayList<Artist>());

        ListView searchListView = (ListView) rootView.findViewById(R.id.artist_search_list_view);
        searchListView.setAdapter(artistSearchAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Click on an artist, send an intent with its id to retrieve its tracks
                Artist artist = (Artist) parent.getItemAtPosition(position);
                if (artist != null && artist.id != null) {
                    Intent intent = new Intent(getActivity(), ArtistTracksActivity.class).putExtra(Intent.EXTRA_TEXT, artist.id);
                    startActivity(intent);
                }
            }
        });

        SearchView searchView = (SearchView) rootView.findViewById(R.id.artist_search);
        searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    artistSearchAdapter.clear();

                    // If there are enough characters, perform a search
                    if (query != null && query.length() >= SEARCH_MIN_CHARS) {
                        SpotifyService spotifyService = new SpotifyApi().getService();

                        spotifyService.searchArtists(query, new Callback<ArtistsPager>() {
                            @Override
                            public void success(ArtistsPager artistsPager, Response response) {
                                if (artistsPager == null || artistsPager.artists == null || artistsPager.artists.items == null || artistsPager.artists.items.isEmpty()) {
                                    // No artists retrieved, show an alert
                                    Toast.makeText(getActivity(), R.string.search_warn_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    artistSearchAdapter.addAll(artistsPager.artists.items);
                                }

                                Log.d(LOG_TAG, "Search artist success. ArtistSearchAdapter refreshed.");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(LOG_TAG, "Search artist failure:" + error.toString());
                            }
                        });
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            }
        );

        return rootView;
    }
}