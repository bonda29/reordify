package tech.bonda.reordify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import tech.bonda.reordify.service.SpotifyClientService;
import tech.bonda.reordify.service.SpotifyPlaylistService;
import tech.bonda.reordify.util.ReflectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPlaylistServiceImpl implements SpotifyPlaylistService {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_THREADS = 5;
    private static final String PLAYLIST_FIELDS = "id,name,images,tracks(total)";

    private final SpotifyClientService clientService;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    @Override
    public List<Playlist> getAllPlaylists(UUID userId) {
        SpotifyApi api = clientService.getApiForUser(userId);
        var paged = api.getListOfCurrentUsersPlaylists()
                .limit(50)
                .build()
                .execute();

        return Arrays.stream(paged.getItems())
                .map(item -> getPlaylist(userId, item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Playlist getPlaylist(UUID userId, String playlistId) {
        SpotifyApi api = clientService.getApiForUser(userId);
        // Initial metadata fetch
        Playlist playlist = api.getPlaylist(playlistId)
                .additionalTypes("track")
                .fields(PLAYLIST_FIELDS)
                .build()
                .execute();

        int total = playlist.getTracks().getTotal();
        int pages = (total + PAGE_SIZE - 1) / PAGE_SIZE;


        List<PlaylistTrack> allTracks = IntStream.range(0, pages)
                .mapToObj(page -> CompletableFuture.supplyAsync(
                        () -> fetchPage(api, playlistId, page * PAGE_SIZE), executor))
                .toList().stream()
                .map(CompletableFuture::join)
                .flatMap(Arrays::stream)
                .toList();

        // Inject full track list
        ReflectionUtil.setPlaylistTracks(playlist, allTracks);
        return playlist;

    }

    private PlaylistTrack[] fetchPage(SpotifyApi api, String playlistId, int offset) {
        return api.getPlaylistsItems(playlistId)
                .fields("items(track)")
                .limit(PAGE_SIZE)
                .offset(offset)
                .build()
                .execute()
                .getItems();
    }
}
