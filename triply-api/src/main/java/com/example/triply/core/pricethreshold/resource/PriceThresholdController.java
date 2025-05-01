package com.example.triply.core.pricethreshold.resource;

import com.example.triply.core.pricethreshold.dto.CreatePriceThresholdRequest;
import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;
import com.example.triply.core.pricethreshold.service.PriceThresholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/priceThreshold") // Using a common base path like /api/v1
public class PriceThresholdController {

    private final PriceThresholdService priceThresholdService;
    
    public PriceThresholdController(PriceThresholdService priceThresholdService) {
        this.priceThresholdService = priceThresholdService;
    }

    /**
     * POST /api/v1/thresholds : Create a new price threshold for the authenticated user.
     *
     * @param request The request body containing threshold details.
     * @return ResponseEntity with the created PriceThresholdDTO (201 Created).
     */
    @PostMapping
    public ResponseEntity<PriceThresholdDTO> createThreshold(@Valid @RequestBody CreatePriceThresholdRequest request) {
        PriceThresholdDTO createdThreshold = priceThresholdService.createThreshold(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdThreshold);
    }

    /**
     * GET /api/v1/thresholds : Get all price thresholds for the authenticated user.
     *
     * @return ResponseEntity with a list of PriceThresholdDTOs (200 OK).
     */
    @GetMapping
    public ResponseEntity<List<PriceThresholdDTO>> getThresholdsForUser() {
        String username = getCurrentUsername();
        List<PriceThresholdDTO> thresholds = priceThresholdService.getThresholdsByUser(username);
        return ResponseEntity.ok(thresholds);
    }

    /**
     * DELETE /api/v1/thresholds/{id} : Delete a specific price threshold by its ID.
     * The service layer ensures the user owns the threshold.
     *
     * @param id The ID of the threshold to delete.
     * @return ResponseEntity with no content (204 No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThreshold(@PathVariable Long id) {
        String username = getCurrentUsername();
        priceThresholdService.deleteThreshold(id, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper method to get the username of the currently authenticated user.
     *
     * @return The username.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // This should ideally not happen due to @PreAuthorize, but good practice
            throw new IllegalStateException("User authentication not found or not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            // Handle cases where principal might be just a String (e.g., in some test setups)
            return principal.toString();
        }
    }
}