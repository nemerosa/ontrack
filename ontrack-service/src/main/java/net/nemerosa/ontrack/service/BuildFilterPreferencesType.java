package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.PreferencesType;
import org.springframework.stereotype.Component;

@Component
public class BuildFilterPreferencesType implements PreferencesType<BuildFilterPreferences> {

    private final ObjectMapper mapper = ObjectMapperFactory.create();

    @Override
    public BuildFilterPreferences fromStorage(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.service.BuildFilterPreferencesType.fromStorage
        return null;
    }

    @Override
    public JsonNode forStorage(BuildFilterPreferences value) {
        return mapper.valueToTree(value);
    }

}
