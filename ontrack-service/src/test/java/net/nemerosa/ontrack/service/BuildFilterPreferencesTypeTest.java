package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.common.MapBuilder;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;
import static org.junit.Assert.assertEquals;

public class BuildFilterPreferencesTypeTest {

    @Test
    public void forStorage() throws Exception {
        BuildFilterPreferencesType type = new BuildFilterPreferencesType();
        JsonNode node = type.forStorage(preferences());
        assertJsonEquals(
                json(),
                node
        );
    }

    @Test
    public void fromStorage() throws Exception {
        BuildFilterPreferencesType type = new BuildFilterPreferencesType();
        BuildFilterPreferences preferences = type.fromStorage(json());
        assertEquals(
                preferences(),
                preferences
        );
    }

    private static BuildFilterPreferences preferences() {
        BuildFilterPreferences preferences = BuildFilterPreferences.empty();
        preferences = preferences.add(1, new BuildFilterPreferencesEntry(
                "Copper since bronze",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", "COPPER")
                        .with("sincePromotionLevel", "BRONZE")
                        .get()
        ));
        preferences = preferences.add(1, new BuildFilterPreferencesEntry(
                "Only copper",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", "COPPER")
                        .get()
        ));
        preferences = preferences.add(2, new BuildFilterPreferencesEntry(
                "Only copper",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", "COPPER")
                        .get()
        ));
        return preferences;
    }

    private static ObjectNode json() {
        return object()
                .with("entries", object()
                        .with("1", object()
                                .with("Copper since bronze", object()
                                        .with("name", "Copper since bronze")
                                        .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                        .with("data", object()
                                                .with("withPromotionLevel", "COPPER")
                                                .with("sincePromotionLevel", "BRONZE")
                                                .end())
                                        .end())
                                .with("Only copper", object()
                                        .with("name", "Only copper")
                                        .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                        .with("data", object()
                                                .with("withPromotionLevel", "COPPER")
                                                .end())
                                        .end())
                                .end())
                        .with("2", object()
                                .with("Only copper", object()
                                        .with("name", "Only copper")
                                        .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                        .with("data", object()
                                                .with("withPromotionLevel", "COPPER")
                                                .end())
                                        .end())
                                .end())
                        .end())
                .end();
    }
}