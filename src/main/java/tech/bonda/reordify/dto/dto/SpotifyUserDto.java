package tech.bonda.reordify.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class SpotifyUserDto {
    private UUID id;
    private String spotifyId;
    private String displayName;
}
