package limechain.etherium.fetcher.db.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import limechain.etherium.fetcher.db.model.ResetPassword;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    ResetPassword findByResetKey(UUID key);

}
