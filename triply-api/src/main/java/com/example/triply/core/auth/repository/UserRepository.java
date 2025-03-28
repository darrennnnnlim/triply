package com.example.triply.core.auth.repository;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.auth.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsernameAndStatus_Status(String username, String status);

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, r.name, COALESCE(u.status.status, 'not assigned')) " +
            "FROM User u JOIN u.roles r")
    List<UserRoleDTO> getUsersWithRoles();

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, r.name, " +
            "CASE WHEN u.status IS NULL THEN 'not assigned' ELSE u.status.status END) " +
            "FROM User u JOIN u.roles r WHERE r.name = 'ROLE_ADMIN'")
    List<UserRoleDTO> getUsersAdminRoles();

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, r.name, u.status.status) FROM User u JOIN u.roles r WHERE u.status.status = 'BANNED'")
    List<UserRoleDTO> getBannedUsers();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = (SELECT us FROM UserStatus us WHERE us.status = 'BANNED') WHERE u.id = :userId")
    void banUser(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = (SELECT us FROM UserStatus us WHERE us.status = 'ACTIVE') WHERE u.id = :userId")
    void unbanUser(Long userId);

}
