//package com.example.triply.core.auth.entity;
//
//import com.example.triply.common.audit.Auditable;
//import com.example.triply.core.admin.entity.UserStatus;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.envers.Audited;
//
//import java.util.Set;
//
//@Entity
//@Table(name = "Users")
//@Getter
//@Setter
//@NoArgsConstructor
//@Audited
//public class User extends Auditable<String> {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "user_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles;  // ✅ Fixed duplicate mapping
//
//    @ManyToOne
//    @JoinColumn(name = "status_id")
//    private UserStatus status; // ✅ Fixed UserStatus mapping
//
//
//    public User(String username) {
//        this.username = username;
//    }
//}

package com.example.triply.core.auth.entity;

import com.example.triply.common.audit.Auditable;
import com.example.triply.core.admin.entity.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private UserStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User(String username) {
        this.username = username;
    }
}
