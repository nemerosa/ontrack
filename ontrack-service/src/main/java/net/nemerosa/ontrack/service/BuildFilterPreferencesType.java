package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.PreferencesType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class BuildFilterPreferencesType implements PreferencesType<BuildFilterPreferences> {

    private final ObjectMapper mapper = ObjectMapperFactory.create();

    @Override
    public BuildFilterPreferences fromStorage(JsonNode node) {
        BuildFilterPreferences preferences = BuildFilterPreferences.empty();
        Iterator<Map.Entry<String, JsonNode>> entries = node.path("entries").fields();
        while (entries.hasNext()) {
            Map.Entry<String, JsonNode> entry = entries.next();
            int branchId = Integer.parseInt(entry.getKey(), 10);
            Iterator<Map.Entry<String, JsonNode>> branchEntries = entry.getValue().fields();
            while (branchEntries.hasNext()) {
                Map.Entry<String, JsonNode> branchEntry = branchEntries.next();
                String name = branchEntry.getKey();
                JsonNode branchEntryData = branchEntry.getValue();
                String type = branchEntryData.path("type").asText();
                if (StringUtils.isNotBlank(type)) {
                    Map<String, String> data = new HashMap<>();
                    Iterator<Map.Entry<String, JsonNode>> dataEntries = branchEntryData.path("data").fields();
                    while (dataEntries.hasNext()) {
                        Map.Entry<String, JsonNode> dataEntry = dataEntries.next();
                        data.put(dataEntry.getKey(), dataEntry.getValue().asText());
                    }
                    preferences = preferences.add(
                            branchId,
                            new BuildFilterPreferencesEntry(
                                    name,
                                    type,
                                    data
                            )
                    );
                }
            }
        }
        // OK
        return preferences;
    }

    @Override
    public JsonNode forStorage(BuildFilterPreferences value) {
        return mapper.valueToTree(value);
    }

}
