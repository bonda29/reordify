package tech.bonda.reordify.util;

import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.lang.reflect.Field;
import java.util.List;

public class ReflectionUtil {

    @SneakyThrows
    public static void setPlaylistTracks(Playlist playlist, List<PlaylistTrack> tracks) {
        Paging<PlaylistTrack> playlistTracks = new Paging.Builder<PlaylistTrack>()
                .setItems(tracks.toArray(new PlaylistTrack[0]))
                .build();

        Field segmentsField = Playlist.class.getDeclaredField("tracks");
        segmentsField.setAccessible(true);
        segmentsField.set(playlist, playlistTracks);
        // Optionally, you can also set the total count if needed
        Field totalField = Paging.class.getDeclaredField("total");
        totalField.setAccessible(true);
        totalField.set(playlistTracks, tracks.size());
    }
}