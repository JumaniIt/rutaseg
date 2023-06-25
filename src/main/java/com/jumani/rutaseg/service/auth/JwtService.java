package com.jumani.rutaseg.service;

public interface JwtService {
    String generateToken(String subject, boolean admin);

    String extractSubject(String token);

    boolean isTokenValid(String token);

    boolean isAdminToken(String token);
}
