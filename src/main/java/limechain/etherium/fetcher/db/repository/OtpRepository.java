package limechain.etherium.fetcher.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import limechain.etherium.fetcher.db.model.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByUserUsernameAndOtp(String name, String otpKey);

    Otp findByUserUsername(String username);
}
