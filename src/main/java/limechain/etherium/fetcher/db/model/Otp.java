package limechain.etherium.fetcher.db.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = BaseEntity.ID_GEN, sequenceName = "otp_seq", allocationSize = 1)
public class Otp extends BaseEntity {

    @OneToOne
    private User user;

    private String otp;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
