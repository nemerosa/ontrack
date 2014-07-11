package net.nemerosa.ontrack.service;

import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class BuildFilterPreferences {

    private final Map<Integer, Map<String, BuildFilterPreferencesEntry>> entries;

    public static BuildFilterPreferences empty() {
        return new BuildFilterPreferences(Collections.emptyMap());
    }

    public BuildFilterPreferences add(int branchId, BuildFilterPreferencesEntry entry) {
        Map<Integer, Map<String, BuildFilterPreferencesEntry>> entries = new HashMap<>(this.entries);
        // Entries for the branch
        Map<String, BuildFilterPreferencesEntry> branchEntries = entries.get(branchId);
        if (branchEntries == null) {
            branchEntries = new HashMap<>();
            entries.put(branchId, branchEntries);
        }
        // Completes with the new entry
        branchEntries.put(entry.getName(), entry);
        // OK
        return new BuildFilterPreferences(entries);
    }

    public Collection<BuildFilterPreferencesEntry> getEntriesForBranch(int branchIdValue) {
        return entries.getOrDefault(
                branchIdValue,
                Collections.emptyMap()
        ).values();
    }
}
