package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.exceptions.JsonWritingException;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.EntityDataService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.repository.EntityDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class EntityDataServiceImpl implements EntityDataService {

    private final EntityDataRepository repository;
    private final SecurityService securityService;
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Autowired
    public EntityDataServiceImpl(EntityDataRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @Override
    public void store(ProjectEntity entity, String key, boolean value) {
        store(entity, key, Objects.toString(value));
    }

    @Override
    public void store(ProjectEntity entity, String key, int value) {
        store(entity, key, Objects.toString(value));
    }

    @Override
    public void store(ProjectEntity entity, String key, Object value) {
        securityService.checkProjectFunction(entity, ProjectConfig.class);
        JsonNode jsonNode = objectMapper.valueToTree(value);
        repository.storeJson(entity, key, jsonNode);
    }

    @Override
    public void store(ProjectEntity entity, String key, String value) {
        securityService.checkProjectFunction(entity, ProjectConfig.class);
        repository.store(entity, key, value);
    }

    @Override
    public Optional<Boolean> retrieveBoolean(ProjectEntity entity, String key) {
        return retrieve(entity, key, Boolean::valueOf);
    }

    @Override
    public Optional<Integer> retrieveInteger(ProjectEntity entity, String key) {
        return retrieve(entity, key, value -> Integer.parseInt(value, 10));
    }

    @Override
    public Optional<String> retrieve(ProjectEntity entity, String key) {
        return retrieve(entity, key, Functions.identity());
    }

    @Override
    public Optional<JsonNode> retrieveJson(ProjectEntity entity, String key) {
        return retrieve(entity, key, value -> {
            try {
                return objectMapper.readTree(value);
            } catch (IOException e) {
                throw new JsonParsingException(e);
            }
        });
    }

    @Override
    public <T> Optional<T> retrieve(ProjectEntity entity, String key, Class<T> type) {
        return retrieve(entity, key, value -> {
            try {
                return objectMapper.readValue(value, type);
            } catch (IOException e) {
                throw new JsonParsingException(e);
            }
        });
    }

    protected <T> Optional<T> retrieve(ProjectEntity entity, String key, Function<String, T> parser) {
        securityService.checkProjectFunction(entity, ProjectView.class);
        return repository.retrieve(entity, key).map(parser::apply);
    }

    @Override
    public void delete(ProjectEntity entity, String key) {
        securityService.checkProjectFunction(entity, ProjectConfig.class);
        repository.delete(entity, key);
    }

    @Override
    public <T> void withData(ProjectEntity entity, String key, Class<T> type, Function<T, T> processFn) {
        retrieve(entity, key, type).ifPresent(data ->
                store(entity, key, processFn.apply(data))
        );
    }
}
