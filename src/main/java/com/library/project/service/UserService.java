package com.library.project.service;

import com.library.project.entity.User;
import com.library.project.exception.UserNotFoundException;
import com.library.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;//
import java.util.Optional;

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
        if (!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$")) {
            throw new IllegalArgumentException("Password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character");
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.STUDENT);
        }

        return userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
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

        //Lombok is not working in this machine
//        existingUser = existingUser.toBuilder()
//                .name(user.getName())
//                .email(user.getEmail())
//                        .build();
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

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
//        if (!borrowRepository.findByUser_UserId(user.getUserId()).isEmpty()) {
//            throw new IllegalStateException("User has active borrow records, cannot be deleted");
//        }

        userRepository.deleteById(id);
    }

    public Optional<User> validateUser(String email, String password) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .filter(user -> user.getPassword().equals(password));
    }

}
