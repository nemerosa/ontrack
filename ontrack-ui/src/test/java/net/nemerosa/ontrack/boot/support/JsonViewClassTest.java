package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.it.AbstractITTestJUnit4Support;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static net.nemerosa.ontrack.model.support.JsonViewClass.getViewClass;
import static org.junit.Assert.assertEquals;

public class JsonViewClassTest {

    @Test
    public void view_class_null() {
        assertEquals(Object.class, getViewClass(null));
    }

    @Test
    public void view_class_collection_empty() {
        assertEquals(Object.class, getViewClass(Arrays.<Project>asList()));
    }

    @Test
    public void view_class_collection() {
        assertEquals(Object.class, getViewClass(Arrays.asList(project())));
    }

    private Project project() {
        return Project.of(AbstractITTestJUnit4Support.nameDescription());
    }

    @Test
    public void view_class_model() {
        assertEquals(Project.class, getViewClass(project()));
    }

    @Test
    public void view_class_resource() {
        assertEquals(Project.class, getViewClass(Resource.of(project(), URI.create("urn:project"))));
    }

    @Test
    public void view_class_resource_collection_empty() {
        assertEquals(Object.class, getViewClass(Resources.of(
                Collections.emptyList(),
                URI.create("urn:projects")
        )));
    }

    @Test
    public void view_class_resource_collection() {
        assertEquals(Object.class, getViewClass(Resources.of(
                Arrays.asList(
                        Resource.of(project(), URI.create("urn:project"))
                ),
                URI.create("urn:projects")
        )));
    }

    @Test
    public void view_class_resource_collection_view_empty() {
        assertEquals(Project.class, getViewClass(Resources.of(
                Collections.emptyList(),
                URI.create("urn:projects")
        ).forView(Project.class)));
    }

    @Test
    public void view_class_resource_collection_view() {
        assertEquals(Project.class, getViewClass(Resources.of(
                Arrays.asList(
                        Resource.of(project(), URI.create("urn:project"))
                ),
                URI.create("urn:projects")
        ).forView(Project.class)));
    }

}
