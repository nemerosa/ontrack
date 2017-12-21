package net.nemerosa.ontrack.extension.vault;

import org.junit.Test;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class VaultConfidentialStoreTest {

    private static final String KEY_NAME = "my-key";
    private static final byte[] KEY_BYTES = "my-super-secret-key".getBytes(Charset.forName("UTF-8"));

    @Test
    public void default_config() throws IOException {
        VaultOperations vaultOperations = mock(VaultOperations.class);
        VaultConfigProperties configProperties = new VaultConfigProperties();
        VaultConfidentialStore store = new VaultConfidentialStore(
                vaultOperations,
                configProperties
        );
        store.store(KEY_NAME, KEY_BYTES);
        verify(vaultOperations, times(1)).write(
                "secret/ontrack/key/my-key",
                new Key(KEY_BYTES)
        );
        VaultResponseSupport<Key> response = new VaultResponseSupport<>();
        response.setData(new Key(KEY_BYTES));
        when(vaultOperations.read("secret/ontrack/key/my-key", Key.class)).thenReturn(
                response
        );
        byte[] bytes = store.load(KEY_NAME);
        assertEquals(KEY_BYTES, bytes);
    }

    @Test
    public void custom_prefix() throws IOException {
        VaultOperations vaultOperations = mock(VaultOperations.class);
        VaultConfigProperties configProperties = new VaultConfigProperties();
        configProperties.setPrefix("custom/keys");
        VaultConfidentialStore store = new VaultConfidentialStore(
                vaultOperations,
                configProperties
        );
        store.store(KEY_NAME, KEY_BYTES);
        verify(vaultOperations, times(1)).write(
                "custom/keys/my-key",
                new Key(KEY_BYTES)
        );
        VaultResponseSupport<Key> response = new VaultResponseSupport<>();
        response.setData(new Key(KEY_BYTES));
        when(vaultOperations.read("custom/keys/my-key", Key.class)).thenReturn(
                response
        );
        byte[] bytes = store.load(KEY_NAME);
        assertEquals(KEY_BYTES, bytes);
    }

}
