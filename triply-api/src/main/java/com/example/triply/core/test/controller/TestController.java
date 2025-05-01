package com.example.triply.core.test.controller;

import com.example.triply.core.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/test")
public class TestController {

    private final AdminService adminService;

    @Autowired
    public TestController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/update-price/{id}/{price}")
    public ResponseEntity<String> testUpdatePrice(@PathVariable Long id, @PathVariable BigDecimal price) {
        try {
            // Assuming updateFlightPrice returns void or some object we don't need for this simple test
            adminService.updateFlightPrice(id, price);
            return ResponseEntity.ok("Successfully updated flight price for ID: " + id + " to " + price);
        } catch (Exception e) {
            // Basic error handling for the test endpoint
            return ResponseEntity.internalServerError().body("Error updating flight price: " + e.getMessage());
        }
    }
}