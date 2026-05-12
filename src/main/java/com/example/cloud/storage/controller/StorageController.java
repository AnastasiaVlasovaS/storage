package com.example.cloud.storage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageController {

    @GetMapping("/")
    public String home() {
        return "Storage app is running!";
    }

    @GetMapping("/api/data")
    public String getData() {
        return "{\"message\": \"Data from storage\"}";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Dashboard (no auth required)";
    }
}
