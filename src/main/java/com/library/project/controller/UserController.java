package com.library.project.controller;

import com.library.project.entity.User;
import com.library.project.exception.UserNotFoundException;
import com.library.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> usersList = userService.getAllUser();
        return ResponseEntity.of(Optional.of(usersList));
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id){
        User u = userService.getUserById(id);
        return ResponseEntity.of(Optional.of(u));
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUsers(@RequestBody User users){
        User createUser = userService.createUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
        }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUsers(@RequestBody User user,@PathVariable("id") Long id){
        User updatedUser =    userService.updateUser(user,id);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUsers(@PathVariable("id") long id){
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("User with id " + id +
                    " has been deleted");
    }
}
