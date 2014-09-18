package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.Replacement;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class CopyServiceImplTest {

    @Test
    public void applyReplacements_none() {
        assertEquals("branches/11.7", CopyServiceImpl.applyReplacements("branches/11.7", Collections.emptyList()));
    }

    @Test
    public void applyReplacements_direct() {
        assertEquals("branches/11.8", CopyServiceImpl.applyReplacements("branches/11.7", Arrays.asList(
                new Replacement("11.7", "11.8")
        )));
    }

    @Test
    public void applyReplacements_several() {
        assertEquals("Release pipeline for branches/11.7", CopyServiceImpl.applyReplacements("Pipeline for trunk", Arrays.asList(
                new Replacement("trunk", "branches/11.7"),
                new Replacement("Pipeline", "Release pipeline")
        )));
    }
}