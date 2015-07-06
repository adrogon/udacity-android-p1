package com.example.android.spotifystreamer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.spotifystreamer.R;
import com.example.android.spotifystreamer.fragments.adapters.ArtistTrackAdapter;
import com.example.android.spotifystreamer.models.ParcelableArtist;
import com.example.android.spotifystreamer.models.ParcelableTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistTracksActivityFragment extends Fragment {
    private final String LOG_TAG = ArtistTracksActivityFragment.class.getSimpleName();

    private final String PARCELABLE_TRACKS_KEY = "parcelableTracks";

    private ArtistTrackAdapter artistTrackAdapter;
    private ArrayList<ParcelableTrack> parcelableTracks;

    public ArtistTracksActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();

        if (intent != null) {

            ParcelableArtist parcelableArtist = intent.getParcelableExtra("parcelableArtist");

            if (parcelableArtist != null && parcelableArtist.id != null && !parcelableArtist.id.isEmpty()) {
                if (parcelableArtist.name != null && !parcelableArtist.name.isEmpty()) {
                    // Sets parcelableArtist.name as Activity's ActionBar's subtitle
                    //noinspection deprecation
                    ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
                    if (actionBarActivity != null) {
                        ActionBar actionBar = actionBarActivity.getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setSubtitle(parcelableArtist.name);
                        }
                    }
                }

                artistTrackAdapter = new ArtistTrackAdapter(getActivity().getBaseContext(), new ArrayList<ParcelableTrack>());

                if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_TRACKS_KEY)) {
                    // If no bundle was saved, perform the request to the API to retrieve tracks
                    searchTracksInSpotifyAPI(parcelableArtist.id);
                } else {
                    // If a bundle was saved, load its tracks
                    parcelableTracks = savedInstanceState.getParcelableArrayList(PARCELABLE_TRACKS_KEY);
                    resetAdapter();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        if (inflater != null) {

            rootView = inflater.inflate(R.layout.fragment_artist_tracks, container, false);

            if (rootView != null) {

                ListView trackListView = (ListView) rootView.findViewById(R.id.artist_track_list_view);

                if (trackListView != null) {
                    trackListView.setAdapter(artistTrackAdapter);
                    trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ParcelableTrack parcelableTrack = (ParcelableTrack) parent.getItemAtPosition(position);
                            if (parcelableTrack != null && parcelableTrack.id != null) {
                                // Click on a track, send an intent with its preview URL to load it in a browser
                                // TODO : Replace by embedded player in Phase 2
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(parcelableTrack.previewUrl));
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            // Saves the tracks into a bundle
            outState.putParcelableArrayList(PARCELABLE_TRACKS_KEY, parcelableTracks);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Resets the artistTrackAdapter with parcelableTracks
     */
    private void resetAdapter() {
        if (artistTrackAdapter != null) {
            artistTrackAdapter.clear();
            if (parcelableTracks != null && !parcelableTracks.isEmpty()) {
                for (ParcelableTrack parcelableTrack : parcelableTracks) {
                    artistTrackAdapter.add(parcelableTrack);
                }
            }
        }
    }

    /**
     * Performs a Spotify API request over /artists/:id/top-tracks and stores result into parcelableTracks
     * @param artistId Artist ID to search tracks from
     */
    private void searchTracksInSpotifyAPI(String artistId) {
        final String SPOTIFY_API_COUNTRY_KEY = "country";
        final String SPOTIFY_API_COUNTRY_VALUE = "SE";

        SpotifyService spotifyService = new SpotifyApi().getService();

        if (spotifyService != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(SPOTIFY_API_COUNTRY_KEY, SPOTIFY_API_COUNTRY_VALUE);
            spotifyService.getArtistTopTrack(artistId, map, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {
                    if (tracks == null || tracks.tracks == null || tracks.tracks.isEmpty()) {
                        // No tracks retrieved, show an alert
                        Toast.makeText(getActivity(), R.string.tracks_warn_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        parcelableTracks = new ArrayList<>();

                        for (Track track : tracks.tracks) {
                            parcelableTracks.add(mapSpotifyTrackToParcelableTrack(track));
                        }

                        resetAdapter();
                    }

                    Log.d(LOG_TAG, "Artist Top Tracks success. ArtistTrackAdapter refreshed.");
                }

                @Override
                public void failure(RetrofitError error) {
                    // Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected exception.
                    Toast.makeText(getActivity(), R.string.connection_issue, Toast.LENGTH_SHORT).show();

                    Log.e(LOG_TAG, "Artist Top Tracks failure:" + error.toString());
                }
            });
        }
    }

    /**
     * Maps a Spotify Track into a ParcelableTrack
     * @param track The Track object coming from the Spotify API wrapper
     * @return      The ParcelableTrack mapped from the Track
     */
    private ParcelableTrack mapSpotifyTrackToParcelableTrack(Track track) {
        ParcelableTrack parcelableTrack = null;

        if (track != null) {
            parcelableTrack = new ParcelableTrack();

            parcelableTrack.id = track.id;
            parcelableTrack.title = track.name;
            parcelableTrack.previewUrl = track.preview_url;

            if (track.album != null) {
                parcelableTrack.album = track.album.name;
                if (track.album.images != null && !track.album.images.isEmpty()) {
                    for (Image image : track.album.images) {
                        if (image != null && image.url != null && !image.url.isEmpty()) {
                            // Keep the smallest image that's at least 200px large
                            if (image.width < 200) {
                                continue;
                            }
                            parcelableTrack.poster = image.url;
                            if (image.width == 200) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return parcelableTrack;
    }
}
