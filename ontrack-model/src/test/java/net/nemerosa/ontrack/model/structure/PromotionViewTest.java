package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.*;

public class PromotionViewTest {

    @Test
    public void no_build_json() throws JsonProcessingException {
        PromotionView view = new PromotionView(
                PromotionLevel.of(
                        Branch.of(
                                Project.of(new NameDescription("P", "Project")).withId(ID.of(1)),
                                new NameDescription("B", "Branch")
                        ).withId(ID.of(1)),
                        new NameDescription("PL", "Promotion level")
                ),
                null,
                null
        );
        assertJsonWrite(
                object()
                        .with("promotionLevel", object()
                                .with("id", 0)
                                .with("name", "PL")
                                .with("description", "Promotion level")
                                .with("image", false)
                                .with("branch", object()
                                        .with("id", 1)
                                        .with("name", "B")
                                        .with("description", "Branch")
                                        .with("project", object()
                                                .with("id", 1)
                                                .with("name", "P")
                                                .with("description", "Project")
                                                .end())
                                        .end())
                                .end())
                        .with("promotedBuild", (String) null)
                        .with("promotionRun", (String) null)
                        .end(),
                view,
                PromotionView.class
        );
    }

    @Test
    public void build_json() throws JsonProcessingException {
        Project project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch branch = Branch.of(project, new NameDescription("B", "Branch")).withId(ID.of(1));
        PromotionLevel promotionLevel = PromotionLevel.of(branch, new NameDescription("PL", "Promotion level"));
        Build build = Build.of(branch, new NameDescription("11", "Build 11"), Signature.of(dateTime(), "User"));
        PromotionView view = new PromotionView(
                promotionLevel,
                build,
                PromotionRun.of(
                        build,
                        promotionLevel,
                        new Signature(
                                dateTime(),
                                new User("user")
                        ),
                        "Promotion"
                )
        );
        assertJsonWrite(
                object()
                        .with("promotionLevel", object()
                                .with("id", 0)
                                .with("name", "PL")
                                .with("description", "Promotion level")
                                .with("image", false)
                                .with("branch", object()
                                        .with("id", 1)
                                        .with("name", "B")
                                        .with("description", "Branch")
                                        .with("project", object()
                                                .with("id", 1)
                                                .with("name", "P")
                                                .with("description", "Project")
                                                .end())
                                        .end())
                                .end())
                        .with("promotedBuild", object()
                                .with("id", 0)
                                .with("name", "11")
                                .with("description", "Build 11")
                                .with("signature", object()
                                        .with("time", dateTimeJson())
                                        .with("user", object()
                                                .with("name", "User")
                                                .end())
                                        .end())
                                        // Branch skipped
                                .end())
                        .with("promotionRun", object()
                                .with("id", 0)
                                        // Promotion level skipped
                                        // Build skipped
                                .with("signature", object()
                                        .with("time", dateTimeJson())
                                        .with("user", object()
                                                .with("name", "user")
                                                .end())
                                        .end())
                                .with("description", "Promotion")
                                .end())
                        .end(),
                view,
                PromotionView.class
        );
    }

}
