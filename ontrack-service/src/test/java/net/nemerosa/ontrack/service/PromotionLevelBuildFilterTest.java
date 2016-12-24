package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PromotionLevelBuildFilterTest {

    @Test
    public void name() {
        assertEquals("Last per promotion level", new PromotionLevelBuildFilterProvider(
                mock(StructureService.class),
                mock(StructureRepository.class)
        ).getName());
    }

}