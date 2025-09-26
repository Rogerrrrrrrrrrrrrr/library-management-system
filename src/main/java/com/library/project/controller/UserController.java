package com.library.project.controller;

import com.library.project.dto.UserRequestDTO;
import com.library.project.dto.UserResponseDTO;
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
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> usersList = userService.getAllUser();
        return ResponseEntity.of(Optional.of(usersList));
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") long id){
        UserResponseDTO u = userService.getUserById(id);
        return ResponseEntity.of(Optional.of(u));
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUsers(@RequestBody UserRequestDTO users){
        UserResponseDTO createUser = userService.createUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
        }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUsers(@RequestBody UserRequestDTO user,@PathVariable("id") Long id){
        UserResponseDTO updatedUser =    userService.updateUser(user,id);
            return ResponseEntity.ok(updatedUser);
        }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUsers(@PathVariable("id") long id){
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("User with id " + id +
                    " has been deleted");
    }
}
