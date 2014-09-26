package net.nemerosa.ontrack.security;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EncryptionServiceImplTest {

    private EncryptionServiceImpl service;
    private ConfidentialKey key;

    @Before
    public void before() {
        key = mock(ConfidentialKey.class);
        service = new EncryptionServiceImpl(key);
    }

    @Test
    public void encrypt() throws Exception {
        service.encrypt("test");
        verify(key, times(1)).encrypt("test");
    }

    @Test
    public void decrypt() throws Exception {
        service.decrypt("xxxx");
        verify(key, times(1)).decrypt("xxxx");
    }

    @Test
    public void encrypt_null() throws Exception {
        service.encrypt(null);
        verify(key, never()).encrypt(anyString());
    }

    @Test
    public void decrypt_null() throws Exception {
        service.decrypt(null);
        verify(key, never()).decrypt(anyString());
    }
}