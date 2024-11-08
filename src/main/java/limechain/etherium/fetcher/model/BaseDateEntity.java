package limechain.etherium.fetcher.model;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Super class for all entities.
 *
 */
@MappedSuperclass
public abstract class BaseDateEntity {

    @CreatedDate
    private Timestamp created;

    public Timestamp getCreated() {
        return created;
    }

    @PrePersist
    protected void onCreate() {
        created = new Timestamp(System.currentTimeMillis());
    }

    @CreatedDate
    private Timestamp updated;

    public Timestamp getUpdated() {
        return updated;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Timestamp(System.currentTimeMillis());
    }
}
