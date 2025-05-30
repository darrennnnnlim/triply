package com.example.triply.core.auth.entity;

import com.example.triply.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.Date;

@Entity
@Table(name = "Refresh_Token")
@Getter
@Setter
@Audited
public class RefreshToken extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @ManyToOne
    private User user;

    private Date expiryDate;

    private boolean revoked;
}
