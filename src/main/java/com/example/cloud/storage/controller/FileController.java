package com.example.cloud.storage.controller;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.service.FileService;
import com.example.cloud.storage.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;  // чтобы получать пользователя по токену

    @GetMapping
    public List<FileDto> getFiles(@RequestHeader("auth-token") String token) {
        return fileService.getFilesByUserToken(token);
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String token,
                                        @RequestParam("file") MultipartFile file) {
        // Получение пользователя по токену
        // Можно передать токен в сервис
        // Логика сохранения файла
        User user = findUserByToken(token);
        fileService.saveFile(user, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token,
                                        @PathVariable Long id) {
        User user = findUserByToken(token);
        fileService.deleteFile(user, id);
        return ResponseEntity.ok().build();
    }

    private User findUserByToken(String token) {
        // Здесь ищите пользователя по токену, например через UserRepository
        // Для примера:
        // return userRepository.findByToken(token);
        // Или используйте TokenService
        return null; // замените на реальную реализацию
    }

}