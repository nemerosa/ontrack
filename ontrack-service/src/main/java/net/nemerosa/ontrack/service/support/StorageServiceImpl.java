package net.nemerosa.ontrack.service.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.support.StorageService;
import net.nemerosa.ontrack.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {

    private final StorageRepository repository;

    @Autowired
    public StorageServiceImpl(StorageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void storeJson(String store, String key, JsonNode node) {
        repository.storeJson(store, key, node);
    }

    @Override
    public Optional<JsonNode> retrieveJson(String store, String key) {
        return repository.retrieveJson(store, key);
    }
}
