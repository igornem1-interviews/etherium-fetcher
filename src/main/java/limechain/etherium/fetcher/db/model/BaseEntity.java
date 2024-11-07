package limechain.etherium.fetcher.db.model;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super class for all entities.
 *
 */
@MappedSuperclass
public abstract class BaseEntity extends BaseDateEntity {

    public static final String ID_GEN = "sequence_generator";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_GEN)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity entity = (BaseEntity) o;
        if (entity.id == null || this.id == null) {
            return false;
        }
        return Objects.equals(entity.id, this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
