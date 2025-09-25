package com.library.project.controller;

import com.library.project.entity.User;
import com.library.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest){
        System.out.println("email is " + loginRequest.getEmail() + "password is " + loginRequest.getPassword());
        User user = userService.validateUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user!=null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }

}
