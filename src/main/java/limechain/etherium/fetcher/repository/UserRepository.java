package limechain.etherium.fetcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import limechain.etherium.fetcher.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
