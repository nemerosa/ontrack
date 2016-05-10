package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static org.junit.Assert.*;

public class BuildLinkPropertyTest {

    @Test
    public void json_deserialise() throws JsonProcessingException {
        assertJsonRead(
                new BuildLinkProperty(
                        Arrays.asList(
                                new BuildLinkPropertyItem("P1", "1"),
                                new BuildLinkPropertyItem("P2", "2")
                        )
                ),
                object()
                        .with("links", array()
                                .with(object().with("project", "P1").with("build", "1").end())
                                .with(object().with("project", "P2").with("build", "2").end())
                                .end())
                        .end(),
                BuildLinkProperty.class
        );
    }

    @Test
    public void match_nok_no_item() {
        assertFalse(
                new BuildLinkProperty(
                        Collections.emptyList()
                ).match("name", "value")
        );
    }

    @Test
    public void match_name_nok() {
        assertFalse(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("nam", "value")
        );
    }

    @Test
    public void match_value_nok_with_exact_value() {
        assertFalse(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", "val")
        );
    }

    @Test
    public void match_value_ok_with_exact_value() {
        assertTrue(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", "value")
        );
    }

    @Test
    public void match_value_ok_with_pattern() {
        assertTrue(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", "val*")
        );
    }

    @Test
    public void match_value_ok_with_wildcard() {
        assertTrue(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", "*")
        );
    }

    @Test
    public void match_value_ok_with_blank() {
        assertTrue(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", "")
        );
    }

    @Test
    public void match_value_ok_with_null() {
        assertTrue(
                new BuildLinkProperty(
                        Collections.singletonList(
                                BuildLinkPropertyItem.of("name", "value")
                        )
                ).match("name", null)
        );
    }

    @Test
    public void removing_duplicates() {
        BuildLinkProperty property = new BuildLinkProperty(
                Arrays.asList(
                        new BuildLinkPropertyItem("P1", "1"),
                        new BuildLinkPropertyItem("P2", "2"),
                        new BuildLinkPropertyItem("P2", "2")
                )
        );
        List<BuildLinkPropertyItem> links = property.getLinks();
        assertEquals(
                Arrays.asList(
                        new BuildLinkPropertyItem("P1", "1"),
                        new BuildLinkPropertyItem("P2", "2")
                ),
                links
        );
    }

}
