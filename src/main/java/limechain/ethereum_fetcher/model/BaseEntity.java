package limechain.ethereum_fetcher.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;

/**
 * Super class for all entities.
 *
 */
@MappedSuperclass
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
