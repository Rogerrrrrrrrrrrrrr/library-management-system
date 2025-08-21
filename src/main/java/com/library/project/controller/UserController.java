package com.library.project.controller;

import com.library.project.entity.User;
import com.library.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/api/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> usersList = userService.getAllUser();
        try {
            if (usersList.size()<=0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.of(Optional.of(usersList));
    }

    @GetMapping(value = "/api/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id){
        User u = userService.getUserById(id);
        if (u==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(Optional.of(u));
    }


    @PostMapping("/api/users")
    public ResponseEntity<User> createUsers(@RequestBody User users){
        User createUser = null;
        try{
            createUser=userService.createUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<User> updateUsers(@RequestBody User user,@PathVariable("id")long id){
        try{
            userService.updateUser(user,id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUsers(@PathVariable("id") long id){
        try {
            this.userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
