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
    void newProject() {
        Event e = Event.newProject(project());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 1
        assert e.renderText() == "New project P."
        assert e.render(testRenderer) == """New project <a href="#/project/1">P</a>."""
    }

    @Test
    void deleteProject() {
        Event e = Event.deleteProject(project());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 0
        assert e.renderText() == "Project P has been deleted."
        assert e.render(testRenderer) == """Project <i class="project">P</i> has been deleted."""
    }

    @Test
    void newBuild() {
        Event e = Event.newBuild(build());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 3
        assert e.renderText() == "New build 1 for branch B in P."
        assert e.render(testRenderer) == """New build <a href="#/build/100">1</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    @Test
    void newPromotionRun() {
        Event e = Event.newPromotionRun(promotionRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 4
        assert e.renderText() == "Build 1 has been promoted to COPPER for branch B in P."
        assert e.render(testRenderer) == """Build <a href="#/build/100">1</a> has been promoted to <a href="#/promotionLevel/100">COPPER</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    @Test
    void newValidationRun() {
        Event e = Event.newValidationRun(validationRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 5
        assert e.renderText() == "Build 1 has run for SMOKE with status Failed in branch B in P."
        assert e.render(testRenderer) == """Build <a href="#/build/100">1</a> has run for <a href="#/validationStamp/100">SMOKE</a> with status <i class="status">Failed</i> in branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
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

    private static PromotionRun promotionRun() {
        def branch = branch();
        PromotionRun.of(
                Build.of(branch, nd("1", "Build"), Signature.of("user")).withId(ID.of(100)),
                PromotionLevel.of(branch, nd("COPPER", "")).withId(ID.of(100)),
                Signature.of("user"),
                ""
        ).withId(ID.of(1000))
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