package com.example.triply;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TriplyApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(TriplyApplicationTests.class);

	@Test
	void contextLoads() {
		LOGGER.info("contextLoads");
	}

}
