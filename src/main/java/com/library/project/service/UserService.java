package com.library.project.service;

import com.library.project.dto.UserRequestDTO;
import com.library.project.dto.UserResponseDTO;
import com.library.project.entity.User;
import com.library.project.exception.UserNotFoundException;
import com.library.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Collections;
import java.util.List;//
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
//@Autowired
//private PasswordEncoder passwordEncoder;
    private final static java.util.logging.Logger log = Logger.getLogger(UserService.class.getName());

    //User Entity --> Response
    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO
                (user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }

    //Request --> User Entity
    public User fromRequestDTO(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole() != null ? dto.getRole() : User.Role.STUDENT);
        return user;
    }


    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info(userRequestDTO.getEmail());
        validateUserForCreate(userRequestDTO);

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warning ("Email already in use: {} " + userRequestDTO.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRequestDTO.getRole() == null) {
            userRequestDTO.setRole(User.Role.STUDENT);
        }
        User user = fromRequestDTO(userRequestDTO);
        String hashedPassword = BCrypt.hashpw(userRequestDTO.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);
        return toResponseDTO(savedUser);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public UserResponseDTO getUserById(Long id) {
       User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

       return toResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUser() {

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.info("No users found in the database");
        }
        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        //Lombok is not working in this machine
//        existingUser = existingUser.toBuilder()
//                .name(user.getName())
//                .email(user.getEmail())
//                        .build();
        existingUser.setName(userRequestDTO.getName());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setRole(userRequestDTO.getRole());
        //only admin can update the role--handled from frontend code
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}" + updatedUser.getEmail());

        return toResponseDTO(updatedUser);    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (user.getRole() == User.Role.ADMIN) {
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
    public boolean validateLogin(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return false;
        return BCrypt.checkpw(password, user.getPassword());
    }


    private void validateUserForCreate(UserRequestDTO user) {
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
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$")) {
            throw new IllegalArgumentException("Password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character");
        }
    }
}
