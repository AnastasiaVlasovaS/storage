package com.example.cloud.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения CloudStorage.
 */
@SpringBootApplication
public class CloudStorageApplication {

	public static void main(String[] args) {
		// Запуск приложения Spring Boot
		SpringApplication.run(CloudStorageApplication.class, args);
		System.out.println("Cloud Storage запущен и готов к работе!");
	}
}
