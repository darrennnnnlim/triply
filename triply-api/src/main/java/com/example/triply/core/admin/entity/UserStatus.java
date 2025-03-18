package com.example.triply.core.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import com.example.triply.core.auth.entity.User;
import java.util.Set;

@Entity
@Table(name = "user_status")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class UserStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String status;

    @OneToMany(mappedBy = "status")
    private Set<User> users;
}