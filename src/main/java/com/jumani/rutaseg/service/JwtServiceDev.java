package com.jumani.rutaseg.service;

import java.util.UUID;

public class JwtServiceDev implements JwtService {
    @Override
    public String generateToken(String subject, boolean admin) {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean isTokenValid(String token) {
        return true;
    }

    @Override
    public boolean isAdminToken(String token) {
        return true;
    }
}
