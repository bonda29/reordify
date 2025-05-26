package tech.bonda.reordify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import tech.bonda.reordify.exception.SpotifyApiException;
import tech.bonda.reordify.service.SpotifyClientService;
import tech.bonda.reordify.service.SpotifyPlaylistService;
import tech.bonda.reordify.util.ReflectionUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPlaylistServiceImpl implements SpotifyPlaylistService {
    private static final int PAGE_SIZE = 100;
    private static final String PLAYLIST_FIELDS = "id,name,images,tracks(total)";

    private final SpotifyClientService clientService;

    @Override
    public List<Playlist> getAllPlaylists(UUID userId) {
        try {
            SpotifyApi api = clientService.getApiForUser(userId);
            Paging<PlaylistSimplified> paged = api.getListOfCurrentUsersPlaylists()
                    .limit(50)
                    .build()
                    .execute();

            return Arrays.stream(paged.getItems())
                    .map(item -> getPlaylist(userId, item.getId()))
                    .toList();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new SpotifyApiException("Failed to fetch playlists for user: " + userId, e);
        }
    }

    @Override
    public Playlist getPlaylist(UUID userId, String playlistId) {
        try {
            SpotifyApi api = clientService.getApiForUser(userId);

            // Fetch just the metadata (including total track count)
            Playlist playlist = api.getPlaylist(playlistId)
                    .additionalTypes("track")
                    .fields(PLAYLIST_FIELDS)
                    .build()
                    .execute();

            int totalTracks = playlist.getTracks().getTotal();
            int pages = (totalTracks + PAGE_SIZE - 1) / PAGE_SIZE;

            List<PlaylistTrack> allTracks = IntStream
                    .range(0, pages)
                    .mapToObj(page -> fetchPageAsync(api, playlistId, page * PAGE_SIZE))
                    .toList()
                    .stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            futures -> CompletableFuture
                                    .allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenApply(ignored ->
                                            futures.stream()
                                                    .map(CompletableFuture::join)
                                                    .flatMap(Arrays::stream)
                                                    .toList()
                                    ).join()
                    ));

            ReflectionUtil.setPlaylistTracks(playlist, allTracks);
            return playlist;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new SpotifyApiException("Failed to fetch playlist: " + playlistId, e);
        }
    }

    private CompletableFuture<PlaylistTrack[]> fetchPageAsync(SpotifyApi api,
                                                              String playlistId,
                                                              int offset) {
        return api.getPlaylistsItems(playlistId)
                .fields("items(track)")
                .limit(PAGE_SIZE)
                .offset(offset)
                .build()
                .executeAsync()
                .thenApply(Paging::getItems);
    }
}