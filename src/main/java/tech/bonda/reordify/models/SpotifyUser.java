package tech.bonda.reordify.models;

import jakarta.persistence.*;
import lombok.*;
import tech.bonda.reordify.common.models.UUIDEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spotify_users")
public class SpotifyUser extends UUIDEntity {
    @Column(name = "spotify_id", unique = true, nullable = false)
    private String spotifyId;

    private String displayName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SpotifyToken token;
}
