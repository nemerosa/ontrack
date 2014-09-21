package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.*
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.*

public class EventTest {

    private final EventRenderer testRenderer = new EventRenderer() {

        @Override
        String render(ProjectEntity e, Event event) {
            switch (e.projectEntityType) {
                case PROJECT:
                    return link(e.name, "project/${e.id}")
                case BRANCH:
                    return link(e.name, "branch/${e.id}")
                case PROMOTION_LEVEL:
                    return link(e.name, "promotionLevel/${e.id}")
                case VALIDATION_STAMP:
                    return link(e.name, "validationStamp/${e.id}")
                case BUILD:
                    return link(e.name, "build/${e.id}")
                case VALIDATION_RUN:
                    return link("#${e.runOrder}", "validationRun/${e.id}")
                default:
                    throw new EventCannotRenderEntityException(event.template, e)
            }
        }

        protected String link(String name, String uri) {
            return """<a href="#/${uri}">${name}</a>"""
        }
    }

    @Test
    void newBuild() {
        Event e = Event.newBuild(build());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.projectEntities.size() == 3
        assert e.renderText() == "New build 1 for branch B in P."
        assert e.render(testRenderer) == """New build <a href="#/build/100">1</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    private static Build build() {
        return Build.of(branch(), nd("1", "Build"), Signature.of("user")).withId(ID.of(100));
    }

    private static Branch branch() {
        return Branch.of(project(), nd("B", "Branch")).withId(ID.of(10));
    }

    private static Project project() {
        return Project.of(nd("P", "Project")).withId(ID.of(1));
    }

}