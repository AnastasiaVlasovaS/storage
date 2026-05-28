package com.example.cloud.storage.controller;

import com.example.cloud.storage.LoginRequest;
import com.example.cloud.storage.exception.*;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        log.info("Попытка входа для пользователя: {}", request.getLogin());

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> {
                    log.warn("Попытка входа с несуществующим логином: {}", request.getLogin());
                    return new UserNotFoundException(request.getLogin());
                });

        if (!request.getPassword().equals(user.getPassword())) {
            log.warn("Неверный пароль для пользователя: {}", request.getLogin());
            throw new InvalidPasswordException();
        }

        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);

        log.info("Успешный вход для пользователя: {}", request.getLogin());

        Map<String, String> response = new HashMap<>();
        response.put("auth-token", token);
        return response;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody LoginRequest request) {
        log.info("Попытка регистрации пользователя: {}", request.getLogin());

        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            log.warn("Попытка регистрации существующего пользователя: {}", request.getLogin());
            throw new UserAlreadyExistsException(request.getLogin());
        }

        User user = new User(request.getLogin(), request.getPassword());
        userRepository.save(user);

        log.info("Успешная регистрация пользователя: {}", request.getLogin());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Пользователь успешно зарегистрирован");
        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("auth-token") String token) {
        log.info("Попытка выхода с токеном: {}", token);

        User user = userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Попытка выхода с невалидным токеном: {}", token);
                    return new UserNotFoundException("с токеном: " + token);
                });

        user.setToken(null);
        userRepository.save(user);

        log.info("Успешный выход пользователя: {}", user.getLogin());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Выход выполнен успешно");
        return response;
    }
}