package com.nimbusnav.flightmanagement;

import com.nimbusnav.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.main.allow-bean-definition-overriding=true",
		"spring.security.enabled=true"
})
class FlightManagementServiceApplicationTests {

	@MockBean
	private SecurityConfig securityConfig; // تعطيل `SecurityConfig` أثناء الاختبار

	@Test
	void contextLoads() {
		System.out.println("ApplicationContext Loaded Successfully!");
	}

	@Configuration
	static class TestConfig {
		@Bean
		public String mockBean() {
			return "test";
		}
	}
}
