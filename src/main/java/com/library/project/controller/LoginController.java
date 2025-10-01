package com.library.project.controller;

import com.library.project.dto.LoginRequest;
import com.library.project.dto.LoginResponse;
import com.library.project.entity.User;
import com.library.project.repository.UserRepository;
import com.library.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;


@RestController
public class LoginController {

    private static final Logger logs = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        boolean valid = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());

        if (valid) {


            User user = userRepository.findByEmail(loginRequest.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            return ResponseEntity.ok(response);

        }

        else {

            Map<String, String> invalidCred = new HashMap<>();
            invalidCred.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidCred);

        }
    }



}
