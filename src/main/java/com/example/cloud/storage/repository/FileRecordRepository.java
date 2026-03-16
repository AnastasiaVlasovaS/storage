package com.example.cloud.storage.repository;

import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByOwner(User owner);
}
