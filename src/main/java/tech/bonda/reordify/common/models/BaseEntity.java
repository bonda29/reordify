package tech.bonda.reordify.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.beans.Transient;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@DynamicUpdate
@MappedSuperclass
@RequiredArgsConstructor
public abstract class BaseEntity<ID extends Serializable> implements Serializable {
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdDate = OffsetDateTime.now();

    @UpdateTimestamp
    @Column(name = "modified_date")
    @Builder.Default
    private OffsetDateTime modifiedDate = OffsetDateTime.now();

    @Transient
    public abstract ID getId();
}
