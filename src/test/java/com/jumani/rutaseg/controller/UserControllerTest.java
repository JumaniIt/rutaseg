package com.jumani.rutaseg.controller;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.UserRequest;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.dto.response.UserResponse;
import com.jumani.rutaseg.dto.result.PaginatedResult;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.repository.UserRepository;
import com.jumani.rutaseg.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserControllerTest {

    @Mock
    UserRepository userRepo;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserController userController;

    @Test
    void create_NotAdminSession_Forbidden() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, false);
        final UserRequest request = new UserRequest();

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> userController.createUser(request, session));

        verifyNoInteractions(userRepo, passwordService);
    }

    @Test
    void createUser_EmailExists_ValidationException() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, true);
        final UserRequest request = new UserRequest("John Doe", "johndoe@example.com", "password", true);

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(request, session));

        assertEquals("user_email_exists", exception.getCode());
        assertEquals("user with the same email already exists", exception.getMessage());

        verify(userRepo).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepo, passwordService);
    }

    @Test
    void createUser_UserCreateOk() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, true);
        final UserRequest request = new UserRequest("John Doe", "johndoe@example.com", "password", true);

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);

        final String encryptedPassword = "encryptedPassword";
        final User newUser = new User(request.getNickname(), encryptedPassword, request.getEmail(), request.isAdmin());
        final User savedUser = new User( request.getNickname(), request.getEmail(),request.getPassword(), request.isAdmin());

        doReturn(encryptedPassword).when(passwordService).encrypt(request.getPassword());
        doReturn(savedUser).when(userRepo).save(any(User.class));

        // Act
        ResponseEntity<UserResponse> response = userController.createUser(request, session);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserResponse expectedUserResponse = new UserResponse(savedUser.getId(), savedUser.getNickname(), savedUser.getEmail(), savedUser.isAdmin());
        assertEquals(expectedUserResponse, response.getBody());

        verify(userRepo).existsByEmail(request.getEmail());
        verify(passwordService).encrypt(request.getPassword());
        verify(userRepo).save(any(User.class));
        verifyNoMoreInteractions(userRepo, passwordService);
    }

    @Test
    void getUserById_NotAdminSession_Forbidden() {
        final SessionInfo session = new SessionInfo(1L, false);
        final Long userId = 1L;

        when(userRepo.findById(userId)).thenThrow(ForbiddenException.class);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> userController.getUserById(userId, session));

        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void getUserById_UserNotFound_NotFoundException() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, true);
        final Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.getUserById(userId, session));

        assertEquals("user not found", exception.getMessage());

        verify(userRepo).findById(userId);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void getUserById_AdminSession_ReturnsUser() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, true);
        final Long userId = 123L;

        User user = mock(User.class); // Crear un objeto User simulado

        when(user.getId()).thenReturn(userId);
        when(user.getNickname()).thenReturn("JohnDoe");
        when(user.getEmail()).thenReturn("johndoe@example.com");
        when(user.isAdmin()).thenReturn(true);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(userId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserResponse expectedUserResponse = new UserResponse(userId, "JohnDoe", "johndoe@example.com", true);
        assertEquals(expectedUserResponse, response.getBody());

        verify(userRepo).findById(userId);
        verifyNoMoreInteractions(userRepo, passwordService);
    }

    @Test
    void searchUsers_NotAdminSession_Forbidden() {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, false);

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> userController.searchUsers(10, 1, true, "John", "john@example.com", session));

        verifyNoInteractions(userRepo);
    }
    @Test
    void searchUsers_AdminSession_ReturnsUsers() throws Exception {
        // Arrange
        final SessionInfo session = new SessionInfo(1L, true);
        final User user1 = new User("John Doe", "admin_password", "john@example.com", true);
        final User user2 = new User("Jane Smith", "regular_password", "jane@example.com", false);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        when(userRepo.count(true, null, null)).thenReturn((long) userList.size());
        when(userRepo.search(true, null, null, 0, 10)).thenReturn(userList);

        // Act
        ResponseEntity<PaginatedResult<UserResponse>> response = userController.searchUsers(10, 1, true, null, null, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PaginatedResult<UserResponse> paginatedResult = response.getBody();
        assertNotNull(paginatedResult, "paginatedResult should not be null");

        List<UserResponse> expectedResponses = userList.stream()
                .map(userController::createResponse)
                .collect(Collectors.toList());

        assertEquals(expectedResponses, paginatedResult.elements());

        verify(userRepo).count(true, null, null);
        verify(userRepo).search(true, null, null, 0, 10);
        verifyNoMoreInteractions(userRepo);
    }

}