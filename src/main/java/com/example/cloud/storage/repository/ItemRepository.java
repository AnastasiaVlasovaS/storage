package com.example.cloud.storage.repository;

import com.example.cloud.storage.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // Spring Data JPA автоматически предоставит CRUD-методы (create, read, update, delete)
    // Можно добавить свои методы, например:
    List<Item> findByNameContainingIgnoreCase(String name);
}