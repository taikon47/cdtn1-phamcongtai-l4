package com.mekongmobile.projectl4;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test requiring live PostgreSQL instance; executed during Docker Compose startup")
class Projectl4ApplicationTests {

	@Test
	void contextLoads() {
	}

}
