package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.support.NameValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class LinkPropertyTypeTest {

    private LinkPropertyType type;

    @Before
    public void before() {
        type = new LinkPropertyType(
                new GeneralExtensionFeature()
        );
    }

    @Test
    public void searchKey_no_link() {
        assertEquals("", type.getSearchKey(new LinkProperty(Collections.emptyList())));
    }

    @Test
    public void searchKey_one_named_link() {
        assertEquals("test", type.getSearchKey(new LinkProperty(Arrays.asList(new NameValue("test", "uri://test")))));
    }

    @Test
    public void searchKey_two_named_links() {
        assertEquals("one,two", type.getSearchKey(new LinkProperty(Arrays.asList(
                new NameValue("one", "uri://test"),
                new NameValue("two", "uri://test")
        ))));
    }

    @Test
    public void replacement() {
        LinkProperty property = LinkProperty.Companion.of("test", "http://wiki/P1");
        assertEquals(
                LinkProperty.Companion.of("test", "http://wiki/P2"),
                type.replaceValue(property, s -> s.replaceAll("P1", "P2"))
        );
    }

}