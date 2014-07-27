package net.nemerosa.ontrack.model.support.tree.support;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MarkupTest {

    @Test
    public void text() {
        Markup m = Markup.text("Test");
        assertNull(m.getType());
        assertEquals("Test", m.getText());
        assertNull(m.getAttributes());
    }

    @Test(expected = NullPointerException.class)
    public void text_no_attr() {
        Markup m = Markup.text("Test");
        m.attr("test", "xxx");
    }

}
