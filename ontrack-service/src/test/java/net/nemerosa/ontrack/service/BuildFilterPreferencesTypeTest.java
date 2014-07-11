package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import org.junit.Test;

import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.json.JsonUtils.stringArray;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;

public class BuildFilterPreferencesTypeTest {

    @Test
    public void forStorage() throws Exception {
        BuildFilterPreferencesType type = new BuildFilterPreferencesType();
        BuildFilterPreferences preferences = BuildFilterPreferences.empty();
        preferences = preferences.add(1, new BuildFilterPreferencesEntry(
                "Copper since bronze",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", Arrays.asList("COPPER"))
                        .with("sincePromotionLevel", Arrays.asList("BRONZE"))
                        .get()
        ));
        preferences = preferences.add(1, new BuildFilterPreferencesEntry(
                "Only copper",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", Arrays.asList("COPPER"))
                        .get()
        ));
        preferences = preferences.add(2, new BuildFilterPreferencesEntry(
                "Only copper",
                StandardBuildFilterProvider.class.getName(),
                MapBuilder.of("withPromotionLevel", Arrays.asList("COPPER"))
                        .get()
        ));
        JsonNode node = type.forStorage(preferences);
        assertJsonEquals(
                object()
                        .with("entries", object()
                                .with("1", object()
                                        .with("Copper since bronze", object()
                                                .with("name", "Copper since bronze")
                                                .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                                .with("data", object()
                                                        .with("withPromotionLevel", stringArray("COPPER"))
                                                        .with("sincePromotionLevel", stringArray("BRONZE"))
                                                        .end())
                                                .end())
                                        .with("Only copper", object()
                                                .with("name", "Only copper")
                                                .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                                .with("data", object()
                                                        .with("withPromotionLevel", stringArray("COPPER"))
                                                        .end())
                                                .end())
                                        .end())
                                .with("2", object()
                                        .with("Only copper", object()
                                                .with("name", "Only copper")
                                                .with("type", "net.nemerosa.ontrack.service.StandardBuildFilterProvider")
                                                .with("data", object()
                                                        .with("withPromotionLevel", stringArray("COPPER"))
                                                        .end())
                                                .end())
                                        .end())
                                .end())
                        .end(),
                node
        );
    }
}