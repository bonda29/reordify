package tech.bonda.reordify.util;

import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionUtilTest {

    @Test
    public void setPlaylistTracks_updatesItemsAndTotal() {
        Playlist playlist = new Playlist.Builder().build();

        PlaylistTrack track1 = new PlaylistTrack.Builder().build();
        PlaylistTrack track2 = new PlaylistTrack.Builder().build();
        List<PlaylistTrack> tracks = Arrays.asList(track1, track2);

        ReflectionUtil.setPlaylistTracks(playlist, tracks);

        Paging<PlaylistTrack> paging = playlist.getTracks();
        assertNotNull(paging, "Paging should not be null");
        assertArrayEquals(tracks.toArray(), paging.getItems());
        assertEquals(2, paging.getTotal());
    }
}
