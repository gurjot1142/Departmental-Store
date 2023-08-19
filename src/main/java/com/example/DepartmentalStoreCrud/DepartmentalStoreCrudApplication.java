package com.example.DepartmentalStoreCrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DepartmentalStoreCrudApplication {
	public static void main(final String[] args) {
		SpringApplication.run(DepartmentalStoreCrudApplication.class, args);
	}
}
