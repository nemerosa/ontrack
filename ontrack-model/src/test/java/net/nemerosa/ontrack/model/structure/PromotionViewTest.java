package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.*;

public class PromotionViewTest {

    @Test
    public void no_build_json() throws JsonProcessingException {
        PromotionView view = new PromotionView(
                new PromotionLevel(ID.NONE, "PL", "Promotion level",
                        new Branch(ID.NONE, "B", "Branch",
                                new Project(ID.NONE, "P", "Project"))
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
                                .with("branch", object()
                                        .with("id", 0)
                                        .with("name", "B")
                                        .with("description", "Branch")
                                        .with("project", object()
                                                .with("id", 0)
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
        Project project = new Project(ID.NONE, "P", "Project");
        Branch branch = new Branch(ID.NONE, "B", "Branch", project);
        PromotionLevel promotionLevel = new PromotionLevel(ID.NONE, "PL", "Promotion level", branch);
        PromotionView view = new PromotionView(
                promotionLevel,
                Build.of(branch, new NameDescription("11", "Build 11"), Signature.of(dateTime(), "User")),
                new PromotionRun(
                        "Promotion",
                        new Signature(
                                dateTime(),
                                new User("user")
                        ),
                        promotionLevel
                )
        );
        assertJsonWrite(
                object()
                        .with("promotionLevel", object()
                                .with("id", 0)
                                .with("name", "PL")
                                .with("description", "Promotion level")
                                .with("branch", object()
                                        .with("id", 0)
                                        .with("name", "B")
                                        .with("description", "Branch")
                                        .with("project", object()
                                                .with("id", 0)
                                                .with("name", "P")
                                                .with("description", "Project")
                                                .end())
                                        .end())
                                .end())
                        .with("promotedBuild", object()
                                .with("id", 0)
                                .with("name", "11")
                                .with("description", "Build 11")
                                        // Branch skipped
                                .end())
                        .with("promotionRun", object()
                                .with("description", "Promotion")
                                .with("signature", object()
                                        .with("time", dateTimeJson())
                                        .with("user", object()
                                                .with("name", "user")
                                                .end())
                                        .end())
                                        // Promotion level skipped
                                .end())
                        .end(),
                view,
                PromotionView.class
        );
    }

}
