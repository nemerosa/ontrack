package net.nemerosa.ontrack.service;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class BuildFilterPreferences {

    private final Map<String, BuildFilterPreferencesEntry> entries;

    public static BuildFilterPreferences empty() {
        return new BuildFilterPreferences(Collections.emptyMap());
    }

    public BuildFilterPreferences add(BuildFilterPreferencesEntry entry) {
        Map<String, BuildFilterPreferencesEntry> entries = new HashMap<>(this.entries);
        entries.put(entry.getName(), entry);
        return new BuildFilterPreferences(entries);
    }
}
