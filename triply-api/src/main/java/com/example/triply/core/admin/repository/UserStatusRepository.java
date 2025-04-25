package com.example.triply.core.admin.repository;

import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.dto.UserRoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    @Query("SELECT us FROM UserStatus us WHERE us.status = :status")
    Optional<UserStatus> findByStatus(String status);

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, COALESCE(u.role.name, 'No Role'), COALESCE(u.status.status, 'not assigned')) " +
            "FROM User u")
    List<UserRoleDTO> getUsersWithRoles();

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, u.role.name, COALESCE(u.status.status, 'not assigned')) " +
            "FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<UserRoleDTO> searchUsersByUsername(@Param("username") String username);

    @Query("SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(u.id, u.username, u.role.name, u.status.status) " +
            "FROM User u WHERE u.status.status = 'BANNED' " +
            "AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<UserRoleDTO> searchBannedUsersByUsername(@Param("username") String username);

}