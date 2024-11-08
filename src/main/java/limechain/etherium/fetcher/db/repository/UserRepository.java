package limechain.etherium.fetcher.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import limechain.etherium.fetcher.db.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
