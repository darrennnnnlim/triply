package com.example.triply.core.admin.service;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.auth.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public List<UserRoleDTO> getUsersWithRoles() {
        String jpql = """
                    SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(
                        u.id, u.username, r.name, COALESCE(s.status, 'not assigned')
                    )
                    FROM User u 
                    JOIN u.roles r 
                    LEFT JOIN u.status s
                """;
        return entityManager.createQuery(jpql, UserRoleDTO.class).getResultList();
    }

    public List<UserRoleDTO> getUsersAdminRoles() {
        String jpql = """
                    SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(
                        u.id, u.username, r.name,
                        CASE WHEN s.status IS NULL THEN 'not assigned' ELSE s.status END
                    )
                    FROM User u 
                    JOIN u.roles r 
                    LEFT JOIN u.status s 
                    WHERE r.name = 'ROLE_ADMIN'
                """;
        return entityManager.createQuery(jpql, UserRoleDTO.class).getResultList();
    }

    public List<UserRoleDTO> getBannedUsers() {
        String jpql = """
                SELECT NEW com.example.triply.core.admin.dto.UserRoleDTO(
                    u.id, u.username, r.name, s.status
                )
                FROM User u 
                JOIN u.roles r 
                JOIN u.status s 
                WHERE s.status = 'BANNED'
            """;
        log.debug("Executing JPQL query: {}", jpql);
        List<UserRoleDTO> result = entityManager.createQuery(jpql, UserRoleDTO.class).getResultList();
        log.debug("Query result size: {}", result.size());
        return result;
    }

    @Transactional
    public void banUser(Long userId) {
        try {
            User user = entityManager.find(User.class, userId);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
            }

            UserStatus bannedStatus = entityManager.createQuery(
                            "SELECT us FROM UserStatus us WHERE us.status = :status",
                            UserStatus.class
                    )
                    .setParameter("status", "BANNED")
                    .getSingleResult();

            user.setStatus(bannedStatus);
            entityManager.merge(user);
            log.info("User with ID {} has been banned successfully.", userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error banning user", e);
        }
    }


    @Transactional
    public void unbanUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }

        UserStatus activeStatus = entityManager.createQuery(
                        "SELECT us FROM UserStatus us WHERE us.status = :status",
                        UserStatus.class
                )
                .setParameter("status", "ACTIVE")
                .getSingleResult();

        user.setStatus(activeStatus);
        entityManager.merge(user);
    }

}
