package com.example.cloud.storage.service;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.exception.*;
import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.FileRecordRepository;
import com.example.cloud.storage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRecordRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    private final String storageDir = "./uploads/";

    public List<FileDto> getFilesByUserToken(String token) {
        log.debug("Получение списка файлов для токена: {}", token);

        User user = userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Пользователь с токеном {} не найден", token);
                    return new UserNotFoundException("с токеном: " + token);
                });

        List<FileRecord> records = fileRepository.findByOwner(user);
        log.info("Найдено {} файлов для пользователя {}", records.size(), user.getLogin());

        return records.stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
    }

    public void saveFile(String token, MultipartFile file) {
        log.info("Сохранение файла {} для токена {}", file.getOriginalFilename(), token);

        User user = userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Пользователь с токеном {} не найден", token);
                    return new UserNotFoundException("с токеном: " + token);
                });

        try {
            // Создаем директорию если не существует
            Path uploadPath = Paths.get(storageDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.debug("Создана директория для загрузок: {}", uploadPath);
            }

            // Генерируем уникальное имя файла
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Сохраняем файл
            file.transferTo(filePath.toFile());
            log.debug("Файл сохранен на диск: {}", filePath);

            // Сохраняем запись в БД
            FileRecord record = new FileRecord();
            record.setFilename(originalFilename);
            record.setFilepath(filePath.toString());
            record.setSize(file.getSize());
            record.setUploadTime(LocalDateTime.now());
            record.setOwner(user);

            fileRepository.save(record);
            log.info("Файл {} успешно сохранен для пользователя {}", originalFilename, user.getLogin());

        } catch (IOException e) {
            log.error("Ошибка при сохранении файла {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new FileStorageException("Ошибка при сохранении файла: " + file.getOriginalFilename(), e);
        }
    }

    public void deleteFile(String token, Long fileId) {
        log.info("Удаление файла с ID {} для токена {}", fileId, token);

        User user = userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Пользователь с токеном {} не найден", token);
                    return new UserNotFoundException("с токеном: " + token);
                });

        FileRecord file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.error("Файл с ID {} не найден", fileId);
                    return new FileNotFoundException(fileId);
                });

        // Проверяем, что файл принадлежит пользователю
        if (!file.getOwner().getId().equals(user.getId())) {
            log.warn("Пользователь {} пытается удалить чужой файл {}", user.getLogin(), fileId);
            throw new FileAccessDeniedException(fileId);
        }

        // Удаляем физический файл
        try {
            Path filePath = Paths.get(file.getFilepath());
            Files.deleteIfExists(filePath);
            log.debug("Физический файл удален: {}", filePath);
        } catch (IOException e) {
            log.error("Ошибка при удалении физического файла {}: {}", file.getFilepath(), e.getMessage(), e);
            throw new FileStorageException("Ошибка при удалении файла: " + file.getFilename(), e);
        }

        // Удаляем запись из БД
        fileRepository.deleteById(fileId);
        log.info("Файл {} успешно удален для пользователя {}", file.getFilename(), user.getLogin());
    }
}