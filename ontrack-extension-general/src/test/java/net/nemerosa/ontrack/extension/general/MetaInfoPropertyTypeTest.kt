package net.nemerosa.ontrack.extension.general;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaInfoPropertyTypeTest {

    private MetaInfoPropertyType type = new MetaInfoPropertyType(
            new GeneralExtensionFeature()
    );

    @Test
    public void containsValueNOKIfWrongFormat() {
        assertFalse(type.containsValue(new MetaInfoProperty(Collections.singletonList(
                MetaInfoPropertyItem.Companion.of("name", "value")
        )), "value"));
    }

    @Test
    public void containsValueNOKIfNotFound() {
        assertFalse(type.containsValue(new MetaInfoProperty(Collections.singletonList(
                MetaInfoPropertyItem.Companion.of("name", "value1")
        )), "name:value"));
    }

    @Test
    public void containsValueOKIfFound() {
        assertTrue(type.containsValue(new MetaInfoProperty(Collections.singletonList(
                MetaInfoPropertyItem.Companion.of("name", "value1")
        )), "name:value1"));
    }

    @Test
    public void containsValueOKIfFoundAmongOthers() {
        assertTrue(type.containsValue(new MetaInfoProperty(Arrays.asList(
                MetaInfoPropertyItem.Companion.of("name1", "value1"),
                MetaInfoPropertyItem.Companion.of("name2", "value2")
        )), "name2:value2"));
    }

}
