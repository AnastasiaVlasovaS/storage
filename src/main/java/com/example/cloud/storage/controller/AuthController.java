package com.example.cloud.storage.controller;

import com.example.cloud.storage.LoginRequest;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("auth-token", token);
        return response;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody LoginRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }

        User user = new User(request.getLogin(), request.getPassword());
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Пользователь успешно зарегистрирован");
        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("auth-token") String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setToken(null);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Выход выполнен успешно");
        return response;
    }
}
