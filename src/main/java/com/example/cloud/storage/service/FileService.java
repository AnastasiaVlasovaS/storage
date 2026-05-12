package com.example.cloud.storage.service;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.FileRecordRepository;
import com.example.cloud.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRecordRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    private final String storageDir = "./uploads/";

    public List<FileDto> getFilesByUserToken(String token) {
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }
        User user = userOptional.get();
        List<FileRecord> records = fileRepository.findByOwner(user);
        return records.stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
    }

    public void saveFile(String token, MultipartFile file) {
        try {
            Optional<User> userOptional = userRepository.findByToken(token);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("Пользователь не найден");
            }

            User user = userOptional.get();

            // Создаем директорию если не существует
            Path uploadPath = Paths.get(storageDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Сохраняем файл
            file.transferTo(filePath.toFile());

            // Сохраняем запись в БД
            FileRecord record = new FileRecord();
            record.setFilename(originalFilename);
            record.setFilepath(filePath.toString());
            record.setSize(file.getSize());
            record.setUploadTime(LocalDateTime.now());
            record.setOwner(user);

            fileRepository.save(record);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    public void deleteFile(String token, Long fileId) {
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        Optional<FileRecord> fileOptional = fileRepository.findById(fileId);
        if (fileOptional.isEmpty()) {
            throw new RuntimeException("Файл не найден");
        }

        FileRecord file = fileOptional.get();

        // Проверяем, что файл принадлежит пользователю
        if (!file.getOwner().getId().equals(userOptional.get().getId())) {
            throw new RuntimeException("Нет доступа к этому файлу");
        }

        // Удаляем физический файл
        try {
            Path filePath = Paths.get(file.getFilepath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage());
        }

        // Удаляем запись из БД
        fileRepository.deleteById(fileId);
    }
}