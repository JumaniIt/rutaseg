package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.UserRequest;
import com.jumani.rutaseg.dto.response.OrderResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.dto.response.UserResponse;
import com.jumani.rutaseg.dto.result.PaginatedResult;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.UserRepository;
import com.jumani.rutaseg.service.PasswordService;
import com.jumani.rutaseg.util.PaginationUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest, @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new ValidationException("user_email_exists", "user with the same email already exists");
        }

        String encryptedPassword = passwordService.encrypt(userRequest.getPassword());
        User newUser = new User(userRequest.getNickname(), encryptedPassword, userRequest.getEmail(), userRequest.isAdmin());
        User savedUser = userRepo.save(newUser);

        UserResponse userResponse = createResponse(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    private UserResponse createResponse(User user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getEmail(), user.isAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id, @Session SessionInfo session) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (!session.admin() && !Objects.equals(user.getId(), session.id())) {
            throw new ForbiddenException();
        }
        UserResponse userResponse = createResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<PaginatedResult<UserResponse>> searchUsers(
            @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "admin", required = false) Boolean admin,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "email", required = false) String email,
            @Session SessionInfo session) {

        if (!session.admin()) {
            throw new ForbiddenException();
        }

        final long totalElements = userRepo.count(admin, nickname, email);

        final PaginatedResult<UserResponse> result = PaginationUtil.get(totalElements, pageSize, page, (startIndex, limit) -> {
            List<User> users = userRepo.search(
                    admin,
                    nickname,
                    email
            );

            return users.stream()
                    .map(this::createResponse)
                    .toList();
        });

        return ResponseEntity.ok(result);

    }
}



