package com.example.cloud.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class AppTester {

	private static final String BASE_URL = "http://localhost:8080";
	private static RestTemplate restTemplate = new RestTemplate();
	private static String authToken; // сюда сохраняем токен после логина

	public static void main(String[] args) {
		try {
			registerUser();
			loginUser();
			uploadFile();
			getFileList();
			logout();
		} catch (Exception e) {
			System.out.println("Ошибка теста: " + e.getMessage());
		}
	}

	// 1. Регистрация (если есть)
	private static void registerUser() {
		String url = BASE_URL + "/register";
		String json = "{\"login\":\"testuser\",\"password\":\"1234\"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(json, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		System.out.println("Регистрация: " + response.getBody());
	}

	// 2. Вход — получение токена
	private static void loginUser() {
		String url = BASE_URL + "/login";
		String json = "{\"login\":\"testuser\",\"password\":\"1234\"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(json, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			// здесь получаем токен из ответа
			// пример: ответ - {"auth-token": "....."}
			String body = response.getBody();
			// очень просто — ищем по строке
			String token = body.substring(body.indexOf(":") + 2, body.length() - 2);
			authToken = token;
			System.out.println("Вход успешен! Токен: " + authToken);
		} else {
			System.out.println("Ошибка входа");
		}
	}

	// 3. Загрузка файла
	private static void uploadFile() {
		String url = BASE_URL + "/upload";

		// читаем файл на диск или создаем пример
		// для простоты — отправляем dummy файл как байты
		byte[] fileData = "Это тестовый файл".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("auth-token", authToken);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("file", new ByteArrayResource(fileData) {
			@Override
			public String getFilename() {
				return "test.txt";
			}
		});

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
		System.out.println("Загрузка файла: " + response.getBody());
	}

	// 4. Получение списка файлов (по желанию)
	private static void getFileList() {
		String url = BASE_URL + "/files";

		HttpHeaders headers = new HttpHeaders();
		headers.set("auth-token", authToken);

		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		System.out.println("Список файлов: " + response.getBody());
	}

	// 5. Выход (если нужно)
	private static void logout() {
		// удалим токен, если есть такая логика
		authToken = null;
		System.out.println("Выход выполнен");
	}
}