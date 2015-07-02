package net.nemerosa.ontrack.extension.general;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaInfoPropertyTest {

    @Test
    public void match_nok_no_item() {
        assertFalse(
                new MetaInfoProperty(
                        Collections.emptyList()
                ).matchNameValue("name", "value")
        );
    }

    @Test
    public void match_name_nok() {
        assertFalse(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("nam", "value")
        );
    }

    @Test
    public void match_value_nok_with_exact_value() {
        assertFalse(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", "val")
        );
    }

    @Test
    public void match_value_ok_with_exact_value() {
        assertTrue(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", "value")
        );
    }

    @Test
    public void match_value_ok_with_pattern() {
        assertTrue(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", "val*")
        );
    }

    @Test
    public void match_value_ok_with_wildcard() {
        assertTrue(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", "*")
        );
    }

    @Test
    public void match_value_ok_with_blank() {
        assertTrue(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", "")
        );
    }

    @Test
    public void match_value_ok_with_null() {
        assertTrue(
                new MetaInfoProperty(
                        Collections.singletonList(
                                MetaInfoPropertyItem.of("name", "value")
                        )
                ).matchNameValue("name", null)
        );
    }
}
