package limechain.ethereum_fetcher.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import limechain.ethereum_fetcher.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findByUsername(String username);

}