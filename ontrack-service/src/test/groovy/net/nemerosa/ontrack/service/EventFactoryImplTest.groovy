package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventCannotRenderEntityException
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.service.events.EventFactoryImpl
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.model.structure.ProjectEntityType.*

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

    @Test
    void deleteProject() {
        Event e = factory.deleteProject(project());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 0
        assert e.renderText() == "Project P has been deleted."
        assert e.render(testRenderer) == """Project <i class="project">P</i> has been deleted."""
    }

    @Test
    void newBuild() {
        Event e = factory.newBuild(build());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 3
        assert e.renderText() == "New build 1 for branch B in P."
        assert e.render(testRenderer) == """New build <a href="#/build/100">1</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    @Test
    void updateBuild() {
        Event e = factory.updateBuild(build());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 3
        assert e.renderText() == "Build 1 for branch B in P has been updated."
    }

    @Test
    void deleteBuild() {
        Event e = factory.deleteBuild(build());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Build 1 for branch B in P has been deleted."
    }

    @Test
    void deletePromotionLevel() {
        Event e = factory.deletePromotionLevel(promotionLevel());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Promotion level COPPER for branch B in P has been deleted."
    }

    @Test
    void reorderPromotionLevels() {
        Event e = factory.reorderPromotionLevels(branch());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Promotion levels for branch B in P have been reordered."
    }

    @Test
    void imageValidationStamp() {
        Event e = factory.imageValidationStamp(validationStamp());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 3
        assert e.renderText() == "Image for validation stamp SMOKE for branch B in P has changed."
    }

    @Test
    void updateValidationStamp() {
        Event e = factory.updateValidationStamp(validationStamp());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 3
        assert e.renderText() == "Validation stamp SMOKE for branch B in P has been updated."
    }

    @Test
    void deleteValidationStamp() {
        Event e = factory.deleteValidationStamp(validationStamp());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Validation stamp SMOKE for branch B in P has been deleted."
    }

    @Test
    void reorderValidationStamps() {
        Event e = factory.reorderValidationStamps(branch());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Validation stamps for branch B in P have been reordered."
    }

    @Test
    void newPromotionRun() {
        Event e = factory.newPromotionRun(promotionRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 4
        assert e.renderText() == "Build 1 has been promoted to COPPER for branch B in P."
        assert e.render(testRenderer) == """Build <a href="#/build/100">1</a> has been promoted to <a href="#/promotionLevel/100">COPPER</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    @Test
    void deletePromotionRun() {
        Event e = factory.deletePromotionRun(promotionRun());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 4
        assert e.renderText() == "Promotion COPPER of build 1 has been deleted for branch B in P."
    }

    @Test
    void newValidationRun() {
        Event e = factory.newValidationRun(validationRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 5
        assert e.renderText() == "Build 1 has run for SMOKE with status Failed in branch B in P."
        assert e.render(testRenderer) == """Build <a href="#/build/100">1</a> has run for <a href="#/validationStamp/100">SMOKE</a> with status <i class="status">Failed</i> in branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>."""
    }

    @Test
    void newValidationRunStatus() {
        Event e = factory.newValidationRunStatus(validationRun());
        assert e != null
        assert e.signature.user.name == 'user'
        assert e.entities.size() == 5
        assert e.renderText() == "Status for SMOKE validation #1 for build 1 in branch B of P has changed to Failed."
        assert e.render(testRenderer) == """Status for <a href="#/validationStamp/100">SMOKE</a> validation <a href="#/validationRun/1000">#1</a> for build <a href="#/build/100">1</a> in branch <a href="#/branch/10">B</a> of <a href="#/project/1">P</a> has changed to <i class="status">Failed</i>."""
    }

    @Test
    void propertyChange_on_promotion_level() {
        Event e = factory.propertyChange(promotionLevel(), new TestPropertyType());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Configuration value property has changed for promotion level COPPER."
    }

    @Test
    void propertyDelete_on_promotion_level() {
        Event e = factory.propertyDelete(promotionLevel(), new TestPropertyType());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 2
        assert e.renderText() == "Configuration value property has been removed from promotion level COPPER."
    }

    @Test
    void propertyChange_on_project() {
        Event e = factory.propertyChange(project(), new TestPropertyType());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 1
        assert e.renderText() == "Configuration value property has changed for project P."
    }

    @Test
    void propertyDelete_on_project() {
        Event e = factory.propertyDelete(project(), new TestPropertyType());
        assert e != null
        assert e.signature == null
        assert e.entities.size() == 1
        assert e.renderText() == "Configuration value property has been removed from project P."
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

    protected static PromotionLevel promotionLevel() {
        PromotionLevel.of(branch(), nd("COPPER", "")).withId(ID.of(100))
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

    protected static ValidationStamp validationStamp() {
        ValidationStamp.of(branch(), nd("SMOKE", "")).withId(ID.of(100))
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
