package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventCannotRenderEntityException
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.service.events.EventFactoryImpl
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.BRANCH
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.BUILD
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.PROJECT
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.PROMOTION_LEVEL
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.VALIDATION_RUN
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.VALIDATION_STAMP

public class EventFactoryImplTest {

    private static EventFactory factory = new EventFactoryImpl();

    @Test
    void newProject() {
        Event e = factory.newProject(project());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 1
        assert e.renderText() == "New project P."
        assert e.render(testRenderer) == """New project <a href="#/project/1">P</a>."""
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

}
