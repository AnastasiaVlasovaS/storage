package com.example.cloud.storage.exception;

public class UserNotFoundException extends StorageException {
    public UserNotFoundException(String login) {
        super("Пользователь не найден: " + login);
    }

    public UserNotFoundException(Long userId) {
        super("Пользователь не найден с ID: " + userId);
    }
}