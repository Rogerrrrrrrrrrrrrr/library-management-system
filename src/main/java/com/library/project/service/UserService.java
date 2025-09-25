package com.library.project.service;

import com.library.project.entity.User;
import com.library.project.exception.UserNotFoundException;
import com.library.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;//"^[A-Za-z0-9+_.-]+@(.+)$"

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.STUDENT);
        }

        return userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        return email.matches("");
    }


    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));


    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User updateUser(User user, Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        existingUser = existingUser.toBuilder()
                .name(user.getName())
                .email(user.getEmail())
                        .build();
        //existingUser.setName(user.getName());
        //existingUser.setEmail(user.getEmail());

        //remove this from here
        existingUser.setRole(user.getRole());
        //only admin can update the role--handled from frontend code
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(()-> new UserNotFoundException("User with id " + id + " not found"));
        if (user.getRole()== User.Role.ADMIN){
            throw new IllegalArgumentException("Admin users cannot be deleted");
        }
        userRepository.deleteById(id);
    }

    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
