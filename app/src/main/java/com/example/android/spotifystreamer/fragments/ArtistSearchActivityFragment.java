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
import com.example.android.spotifystreamer.models.ParcelableArtist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistSearchActivityFragment extends Fragment {
    private String LOG_TAG = ArtistSearchActivityFragment.class.getSimpleName();

    private final String PARCELABLE_ARTISTS_KEY = "parcelableArtists";

    private ArtistSearchAdapter artistSearchAdapter;
    private ArrayList<ParcelableArtist> parcelableArtists;

    public ArtistSearchActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artistSearchAdapter = new ArtistSearchAdapter(getActivity().getBaseContext(), new ArrayList<ParcelableArtist>());

        if (savedInstanceState != null && savedInstanceState.containsKey(PARCELABLE_ARTISTS_KEY)) {
            // If a bundle was saved, load its artists
            parcelableArtists = savedInstanceState.getParcelableArrayList(PARCELABLE_ARTISTS_KEY);
            resetAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        if (inflater != null) {

            rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);

            if (rootView != null) {

                ListView searchListView = (ListView) rootView.findViewById(R.id.artist_search_list_view);

                if (searchListView != null) {
                    searchListView.setAdapter(artistSearchAdapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Click on an artist, send an intent holding it to retrieve its tracks
                            ParcelableArtist parcelableArtist = (ParcelableArtist) parent.getItemAtPosition(position);
                            if (parcelableArtist != null && parcelableArtist.id != null) {
                                Intent intent = new Intent(getActivity(), ArtistTracksActivity.class).putExtra("parcelableArtist", parcelableArtist);
                                startActivity(intent);
                            }
                        }
                    });
                }

                SearchView searchView = (SearchView) rootView.findViewById(R.id.artist_search);

                if (searchView != null) {
                    searchView.setOnQueryTextListener(
                            new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    if (query != null && !query.isEmpty()) {
                                        searchArtistInSpotifyAPI(query);
                                    }

                                    return true;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    return false;
                                }
                            }
                    );
                }
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            // Saves the artists into a bundle
            outState.putParcelableArrayList(PARCELABLE_ARTISTS_KEY, parcelableArtists);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Resets the artistSearchAdapter with parcelableArtists
     */
    private void resetAdapter() {
        if (artistSearchAdapter != null) {
            artistSearchAdapter.clear();
            if (parcelableArtists != null && !parcelableArtists.isEmpty()) {
                for (ParcelableArtist parcelableArtist : parcelableArtists) {
                    artistSearchAdapter.add(parcelableArtist);
                }
            }
        }
    }

    /**
     * Performs a Spotify API request over /search?type=artist and stores result into parcelableArtists
     * @param query Artist full name to be queried
     */
    private void searchArtistInSpotifyAPI(String query) {
        if (query != null && !query.isEmpty()) {
            if (artistSearchAdapter != null) {
                artistSearchAdapter.clear();
            }

            SpotifyService spotifyService = new SpotifyApi().getService();

            if (spotifyService != null) {
                spotifyService.searchArtists(query, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        if (artistsPager == null || artistsPager.artists == null || artistsPager.artists.items == null || artistsPager.artists.items.isEmpty()) {
                            // No artists retrieved, show an alert
                            Toast.makeText(getActivity(), R.string.search_warn_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            parcelableArtists = new ArrayList<>();

                            for (Artist artist : artistsPager.artists.items) {
                                parcelableArtists.add(mapSpotifyArtistToParcelableArtist(artist));
                            }

                            resetAdapter();
                        }

                        Log.d(LOG_TAG, "Search artist success. ArtistSearchAdapter refreshed.");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected exception.
                        Toast.makeText(getActivity(), R.string.connection_issue, Toast.LENGTH_SHORT).show();

                        Log.e(LOG_TAG, "Search artist failure:" + error.toString());
                    }
                });
            }
        }
    }

    /**
     * Maps a Spotify Artist into a ParcelableArtist
     * @param artist    The Artist object coming from the Spotify API wrapper
     * @return          The ParcelableArtist mapped from the Artist
     */
    private ParcelableArtist mapSpotifyArtistToParcelableArtist(Artist artist) {
        ParcelableArtist parcelableArtist = null;

        if (artist != null) {
            parcelableArtist = new ParcelableArtist();
            parcelableArtist.id = artist.id;
            parcelableArtist.name = artist.name;

            if (artist.images != null && !artist.images.isEmpty()) {
                for (Image image : artist.images) {
                    if (image != null && image.url != null && !image.url.isEmpty() && image.width != null && image.width > 0) {
                        // Keep the smallest image that's at least 200px large
                        if (image.width < 200) {
                            continue;
                        }
                        parcelableArtist.poster = image.url;
                        if (image.width == 200) {
                            break;
                        }
                    }
                }
            }
        }

        return parcelableArtist;
    }
}
