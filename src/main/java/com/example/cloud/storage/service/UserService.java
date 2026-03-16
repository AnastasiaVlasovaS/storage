package com.example.cloud.storage.service;

import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByLogin(String login) {
        Optional<User> userOptional = userRepository.findByLogin(login);
        return userOptional.orElse(null);
    }

    public boolean userExists(String login) {
        return userRepository.findByLogin(login).isPresent();
    }

    public User createUser(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        return userRepository.save(user);
    }
}
