package com.example.bdroomcomcamera;

import com.example.bdroomcomcamera.utils.SecurityUtils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SecurityUtilsTest {

    @Test
    public void emailValido_deveSerAceito() {
        assertTrue(SecurityUtils.isValidEmail("aluno@ufms.br"));
    }

    @Test
    public void emailInvalido_deveSerRejeitado() {
        assertFalse(SecurityUtils.isValidEmail("aluno-sem-arroba"));
    }

    @Test
    public void senhaFraca_deveSerRejeitada() {
        assertFalse(SecurityUtils.isStrongPassword("123456"));
        assertFalse(SecurityUtils.isStrongPassword("abcdefgh"));
        assertFalse(SecurityUtils.isStrongPassword("ABCDEFGH"));
    }

    @Test
    public void senhaForte_deveSerAceita() {
        assertTrue(SecurityUtils.isStrongPassword("Senha@123"));
    }

    @Test
    public void hashNaoDeveGuardarSenhaEmTextoPuro() {
        String hash = SecurityUtils.hashPassword("Senha@123");
        assertNotEquals("Senha@123", hash);
        assertTrue(SecurityUtils.isSha256Hash(hash));
        assertTrue(SecurityUtils.matchesPassword("Senha@123", hash));
        assertFalse(SecurityUtils.matchesPassword("Errada@123", hash));
    }
}
