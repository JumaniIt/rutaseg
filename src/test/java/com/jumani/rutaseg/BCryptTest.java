package com.jumani.rutaseg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BCrypt usage test tutorial")
public class BCryptTest {
    
    @Test
    void sampleTest() {
        final String passwordPlainText = "1234";

        // encripto texto plano usando BCrypt
        final String passwordEncoded = BCrypt.hashpw(passwordPlainText, BCrypt.gensalt());

        // comparo el texto plano contra el password encoded usando BCrypt
        // el passwordPlainText es lo que recibimos en la request y passwordEncoded es
        // lo que tiene guardado el user que recupero de la BD
        assertTrue(BCrypt.checkpw(passwordPlainText, passwordEncoded));
    }
}
