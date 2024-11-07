package limechain.etherium.fetcher.db.model;

import java.sql.Timestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.annotation.CreatedDate;

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
