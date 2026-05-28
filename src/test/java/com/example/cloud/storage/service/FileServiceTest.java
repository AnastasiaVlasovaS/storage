package com.example.cloud.storage.service;

import com.example.cloud.storage.FileDto;
import com.example.cloud.storage.exception.*;
import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.FileRecordRepository;
import com.example.cloud.storage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRecordRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileService fileService;

    @TempDir
    Path tempDir;

    private User user;
    private FileRecord fileRecord;
    private String token;

    @BeforeEach
    void setUp() {
        token = "valid-token";
        user = new User("testuser", "password");
        user.setId(1L);
        user.setToken(token);

        fileRecord = new FileRecord();
        fileRecord.setId(1L);
        fileRecord.setFilename("test.txt");
        fileRecord.setSize(1024L);
        fileRecord.setOwner(user);
        fileRecord.setFilepath(tempDir.resolve("test.txt").toString());
    }

    @Test
    void getFilesByUserToken_Success() {
        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(fileRepository.findByOwner(user)).thenReturn(Arrays.asList(fileRecord));

        List<FileDto> files = fileService.getFilesByUserToken(token);

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getName());
    }

    @Test
    void getFilesByUserToken_UserNotFound_ThrowsException() {
        when(userRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> fileService.getFilesByUserToken(token));
        verify(fileRepository, never()).findByOwner(any());
    }

    @Test
    void saveFile_Success() {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(fileRepository.save(any(FileRecord.class))).thenReturn(fileRecord);

        assertDoesNotThrow(() -> fileService.saveFile(token, mockFile));
        verify(fileRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    void saveFile_UserNotFound_ThrowsException() {
        MultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        when(userRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> fileService.saveFile(token, mockFile));
        verify(fileRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void deleteFile_Success() {
        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(fileRepository.findById(1L)).thenReturn(Optional.of(fileRecord));
        doNothing().when(fileRepository).deleteById(1L);

        assertDoesNotThrow(() -> fileService.deleteFile(token, 1L));
        verify(fileRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFile_FileNotFound_ThrowsException() {
        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.deleteFile(token, 999L));
        verify(fileRepository, never()).deleteById(any());
    }

    @Test
    void deleteFile_AccessDenied_ThrowsException() {
        User otherUser = new User("otheruser", "password");
        otherUser.setId(2L);
        fileRecord.setOwner(otherUser);

        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(fileRepository.findById(1L)).thenReturn(Optional.of(fileRecord));

        assertThrows(FileAccessDeniedException.class, () -> fileService.deleteFile(token, 1L));
        verify(fileRepository, never()).deleteById(any());
    }
}