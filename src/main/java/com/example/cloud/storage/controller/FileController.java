package com.example.cloud.storage.controller;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> listFiles(@RequestHeader("auth-token") String token) {
        List<FileDto> files = fileService.getFilesByUserToken(token);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("file") MultipartFile file) {

        fileService.saveFile(token, file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Файл успешно загружен");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @RequestHeader("auth-token") String token,
            @PathVariable Long fileId) {

        fileService.deleteFile(token, fileId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Файл успешно удален");
        return ResponseEntity.ok(response);
    }
}