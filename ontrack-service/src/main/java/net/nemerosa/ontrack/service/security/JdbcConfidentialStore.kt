package net.nemerosa.ontrack.service.security;

import lombok.Data;
import net.nemerosa.ontrack.model.security.AbstractConfidentialStore;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.model.support.StorageService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Storing the keys in the database.
 * <p>
 * This is easy to setup but is NOT secure.
 */
@Component
@ConditionalOnProperty(name = OntrackConfigProperties.KEY_STORE, havingValue = "jdbc")
public class JdbcConfidentialStore extends AbstractConfidentialStore {

    private final StorageService storageService;

    @Autowired
    public JdbcConfidentialStore(StorageService storageService) {
        this.storageService = storageService;
        LoggerFactory.getLogger(JdbcConfidentialStore.class).info(
                "[key-store] Using JDBC based key store"
        );
    }

    @Override
    public void store(String key, byte[] payload) throws IOException {
        storageService.store(
                JdbcConfidentialStore.class.getSimpleName(),
                key,
                new Key(payload)
        );
    }

    @Override
    public byte[] load(String key) throws IOException {
        return storageService.retrieve(
                JdbcConfidentialStore.class.getSimpleName(),
                key,
                Key.class
        )
                .map(Key::getPayload)
                .orElse(null);
    }

    @Data
    public static class Key {
        private final byte[] payload;
    }
}
