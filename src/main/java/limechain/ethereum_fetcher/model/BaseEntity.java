package limechain.ethereum_fetcher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Super class for all entities.
 *
 */
@MappedSuperclass
@EqualsAndHashCode(of = "id")
@Getter
public abstract class BaseEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
}
