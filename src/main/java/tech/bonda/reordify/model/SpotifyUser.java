package tech.bonda.reordify.model;

import jakarta.persistence.*;
import lombok.*;
import tech.bonda.reordify.model.common.UUIDEntity;

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

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private SpotifyToken token;
}