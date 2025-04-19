package com.example.triply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync; // Added this import

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync // Added this annotation
public class TriplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriplyApplication.class, args);
	}

}
