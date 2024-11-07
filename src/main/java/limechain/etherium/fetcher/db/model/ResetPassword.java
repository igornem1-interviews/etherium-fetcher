package limechain.etherium.fetcher.db.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class ResetPassword extends BaseDateEntity {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "user_id")
    private Account user;

    @Column(unique = true)
    private UUID resetKey;

    public ResetPassword() {
    }

    public ResetPassword(Account user, UUID resetKey) {
        this.user = user;
        this.id = user.getId();
        this.resetKey = resetKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public UUID getResetKey() {
        return resetKey;
    }

    public void setResetKey(UUID resetKey) {
        this.resetKey = resetKey;
    }

}
