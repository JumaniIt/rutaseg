package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.UserRequest;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.dto.response.UserResponse;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.UserRepository;
import com.jumani.rutaseg.service.PasswordService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Transactional
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;

    private final PasswordService passwordService;

    public UserController(UserRepository userRepo,
                          PasswordService passwordService) {

        this.userRepo = userRepo;
        this.passwordService = passwordService;
    }
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest, @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new ValidationException("user_email_exists", "user with the same email already exists");
        }

        String encryptedPassword = passwordService.encrypt(userRequest.getPassword());
        User newUser = new User(userRequest.getName(), encryptedPassword, userRequest.getEmail(), userRequest.isAdmin());
        User savedUser = userRepo.save(newUser);

        UserResponse userResponse = createResponse(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    private UserResponse createResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.isAdmin());
    }
}





