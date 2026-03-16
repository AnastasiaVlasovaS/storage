package com.example.cloud.storage.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public SomeController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }



}