package limechain.etherium_fetcher.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = User.TABLE_NAME)
@Entity
public class User {
    static final String TABLE_NAME = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false) private Integer id;

    @Column(nullable = false) private String fullName;

    @Column(unique = true, length = 100, nullable = false) private String email;

    @Column(nullable = false) private String password;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at") private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") private Date updatedAt;

    // Getters and setters
}