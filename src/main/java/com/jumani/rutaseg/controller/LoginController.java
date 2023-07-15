package com.jumani.rutaseg.controller;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.LoginRequest;
import com.jumani.rutaseg.dto.response.LoginResponse;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.UserRepository;
import com.jumani.rutaseg.service.auth.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jumani.rutaseg.service.PasswordService;
import com.jumani.rutaseg.dto.response.UserResponse;
import java.util.Optional;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.dto.response.SessionInfo;

@RestController
@Transactional
@RequestMapping("/login")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public LoginController(UserRepository userRepository,
                           PasswordService passwordService,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, @Session SessionInfo session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> optionalUser = userRepository.findOneByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Validar la contraseña utilizando PasswordService.matches

            if (passwordService.matches(password, user.getPassword())) {
                // La contraseña coincide, generar el token de sesión
                String token = jwtService.generateToken(user.getId().toString(), session.admin());

                // Crear la respuesta LoginResponse
                UserResponse userResponse = new UserResponse(user.getId(), user.getNickname(), user.getEmail(), user.isAdmin());
                LoginResponse loginResponse = new LoginResponse(token, userResponse);

                // Devolver la respuesta exitosa
                return ResponseEntity.ok(loginResponse);
        }
    }
        throw new ValidationException("invalid_credentials", "Invalid email or password");

    }
}
