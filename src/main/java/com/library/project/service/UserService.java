package com.library.project.service;

import com.library.project.entity.User;
import com.library.project.exception.UserNotFoundException;
import com.library.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        return this.userRepository.save(user);
    }
    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUser(){
        return this.userRepository.findAll();
    }

    public User updateUser(User user,Long id){
        User existingUser =   userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found with id: " + id));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
