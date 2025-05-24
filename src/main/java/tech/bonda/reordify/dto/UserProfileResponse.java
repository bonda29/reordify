package tech.bonda.reordify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String spotifyId;
    private String displayName;
    private String email;
}