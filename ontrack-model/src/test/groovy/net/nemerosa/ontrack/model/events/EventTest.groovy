package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NameValue
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

        @Override
        String render(String valueKey, NameValue value, Event event) {
            """<i class="${valueKey}">${value.value}</i>"""
        }

        protected String link(String name, String uri) {
            return """<a href="#/${uri}">${name}</a>"""
        }
    }

    @Test
    void newValidationRunStatus() {
        Event e = Event.newValidationRunStatus(validationRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 5
        assert e.renderText() == "Status for SMOKE validation #1 for build 1 in branch B of P has changed to Failed."
        assert e.render(testRenderer) == """Status for <a href="#/validationStamp/100">SMOKE</a> validation <a href="#/validationRun/1000">#1</a> for build <a href="#/build/100">1</a> in branch <a href="#/branch/10">B</a> of <a href="#/project/1">P</a> has changed to <i class="status">Failed</i>."""
    }

    private static ValidationRun validationRun() {
        def branch = branch();
        ValidationRun.of(
                Build.of(branch, nd("1", "Build"), Signature.of("user")).withId(ID.of(100)),
                ValidationStamp.of(branch, nd("SMOKE", "")).withId(ID.of(100)),
                1,
                Signature.of("user"),
                ValidationRunStatusID.STATUS_FAILED,
                ""
        ).withId(ID.of(1000))
    }

    private static Branch branch() {
        return Branch.of(project(), nd("B", "Branch")).withId(ID.of(10));
    }

    private static Project project() {
        return Project.of(nd("P", "Project")).withId(ID.of(1));
    }

}