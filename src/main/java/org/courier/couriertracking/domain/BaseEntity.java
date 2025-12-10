package org.courier.couriertracking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "create_time", nullable = false, updatable = false)
    private Instant createTime;

    @Column(name = "create_user", nullable = false, updatable = false, length = 100)
    private String createUser;

    @Column(name = "last_modified_time")
    private Instant lastModifiedTime;

    @Column(name = "last_modified_user", length = 100)
    private String lastModifiedUser;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();   // UUID generate
        }
        if (createTime == null) {
            createTime = now;
        }
        if (createUser == null) {
            createUser = "system";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedTime = Instant.now();
        if (lastModifiedUser == null) {
            lastModifiedUser = "system";
        }
    }
}
