package com.example.triply.core.booking.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${triply.api-version}/booking")
public class BookingResource {

    @PostMapping("/test")
    public ResponseEntity<?> postTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
