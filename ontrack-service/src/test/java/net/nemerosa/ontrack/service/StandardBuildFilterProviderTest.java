package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class StandardBuildFilterProviderTest {

    private StandardBuildFilterProvider provider;

    @Before
    public void before() {
        StructureService structureService = mock(StructureService.class);
        ValidationRunStatusService validationRunStatusService = mock(ValidationRunStatusService.class);
        provider = new StandardBuildFilterProvider(
                structureService,
                validationRunStatusService
        );
    }

    @Test
    public void parse_count_only() {
        Optional<StandardBuildFilterData> data = provider.parse(JsonUtils.object().with("count", 5).end());
        assertTrue(data.isPresent());
        assertEquals(5, data.get().getCount());
        assertNull(data.get().getWithPromotionLevel());
    }

    @Test
    public void parse_with_promotion_level_null() {
        Optional<StandardBuildFilterData> data = provider.parse(JsonUtils.object()
                .with("count", 5)
                .with("withPromotionLevel", (String) null)
                .end());
        assertTrue(data.isPresent());
        assertEquals(5, data.get().getCount());
        assertNull(data.get().getWithPromotionLevel());
    }

}