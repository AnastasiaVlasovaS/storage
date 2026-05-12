package com.example.cloud.storage.repository;

import com.example.cloud.storage.model.FileRecord;
import com.example.cloud.storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByOwner(User owner);
}