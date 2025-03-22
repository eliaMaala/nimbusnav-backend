package com.nimbusnav.flightmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.nimbusnav")
@ComponentScan(basePackages = "com.nimbusnav") // Make sure to clear all packages including config
@EnableScheduling // ✅ هذا ضروري لتفعيل المهام المجدولة
@EnableJpaRepositories("com.nimbusnav.flightmanagement.repositories")
@EntityScan(basePackages = "com.nimbusnav.flightmanagement.models")



public class FlightManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightManagementServiceApplication.class, args);
	}


}
