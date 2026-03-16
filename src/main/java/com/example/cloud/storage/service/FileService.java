package com.example.cloud.storage.service;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.FileRecordRepository;
import com.example.cloud.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRecordRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    // Временное хранение папки с файлами (прочитаете из настроек)
    private final String storageDir = "/var/app/files";

    // Метод для получения списков файлов пользователя
    public List<FileDto> listFiles(User user) {
        List<FileRecord> records = fileRepository.findByOwner(user);
        return records.stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
    }

    // Метод для получения файла по токену - добавляем
    public List<FileDto> getFilesByUserToken(String token) {
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }
        return listFiles(userOptional.get());
    }

    public void saveFile(User user, MultipartFile file) {
        // Тут логика сохранения файла на диск и записи в базу
        // Например:
        // 1. сохраняем файл на диск
        // 2. создаем FileRecord и сохраняем в базу
    }

    public void deleteFile(User user, Long fileId) {
        // Здесь логика удаления файла по ID, проверка владельца
    }
}