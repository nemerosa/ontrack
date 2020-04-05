package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class ProjectStatusViewTest {

    @Test
    public void project_status_view_to_json() throws JsonProcessingException {
        Project project = Project.of(nd("PRJ", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch branch = Branch.of(project, nd("master", "")).withId(ID.of(10)).withSignature(SIGNATURE);

        PromotionLevel silver = PromotionLevel.of(branch, nd("silver", "")).withId(ID.of(100)).withSignature(SIGNATURE);

        Signature signature = Signature.of("test").withTime(LocalDateTime.of(2016, 3, 24, 14, 36));

        Build latestBuild = Build.of(branch, nd("2", ""), signature).withId(ID.of(1000));
        Build promotedBuild = Build.of(branch, nd("1", ""), signature).withId(ID.of(999));

        BranchStatusView branchStatusView = new BranchStatusView(
                branch,
                Collections.emptyList(),
                latestBuild,
                Collections.singletonList(
                        new PromotionView(
                                silver,
                                PromotionRun.of(
                                        promotedBuild,
                                        silver,
                                        signature,
                                        ""
                                )
                        )
                )
        );

        ProjectStatusView projectStatusView = new ProjectStatusView(
                project,
                Collections.emptyList(),
                Collections.singletonList(
                        branchStatusView
                )
        );
        ObjectNode expected = object()
                .with("project", object()
                        .with("id", 1)
                        .with("name", "PRJ")
                        .with("description", "Project")
                        .with("disabled", false)
                        .with("signature", SIGNATURE_OBJECT)
                        .end()
                )
                .with("decorations", array().end())
                .with("branchStatusViews", array()
                        .with(
                                object().with("branch", object()
                                        .with("id", 10)
                                        .with("name", "master")
                                        .with("description", "")
                                        .with("disabled", false)
                                        .with("signature", SIGNATURE_OBJECT)
                                        .end()
                                )
                                        .with("decorations", array().end())
                                        .with("latestBuild", object()
                                                .with("id", 1000)
                                                .with("name", "2")
                                                .with("description", "")
                                                .with("signature", object()
                                                        .with("time", "2016-03-24T14:36:00Z")
                                                        .with("user", object().with("name", "test").end())
                                                        .end()
                                                )
                                                .end()
                                        )
                                        .with("promotions", array()
                                                .with(
                                                        object()
                                                                .with("promotionLevel", object()
                                                                        .with("id", 100)
                                                                        .with("name", "silver")
                                                                        .with("description", "")
                                                                        .with("image", false)
                                                                        .with("signature", SIGNATURE_OBJECT)
                                                                        .end()
                                                                )
                                                                .with("promotionRun", object()
                                                                        .with("build", object()
                                                                                .with("id", 999)
                                                                                .with("name", "1")
                                                                                .with("description", "")
                                                                                .with("signature", object()
                                                                                        .with("time", "2016-03-24T14:36:00Z")
                                                                                        .with("user", object().with("name", "test").end())
                                                                                        .end()
                                                                                )
                                                                                .end()
                                                                        )
                                                                        .with("description", "")
                                                                        .with("id", 0)
                                                                        .with("signature", object()
                                                                                .with("time", "2016-03-24T14:36:00Z")
                                                                                .with("user", object().with("name", "test").end())
                                                                                .end()
                                                                        )
                                                                        .end()
                                                                )
                                                                .end()
                                                )
                                                .end()
                                        )
                                        .with("lastPromotionView", object()
                                                .with("promotionLevel", object()
                                                        .with("id", 100)
                                                        .with("name", "silver")
                                                        .with("description", "")
                                                        .with("image", false)
                                                        .with("signature", SIGNATURE_OBJECT)
                                                        .end()
                                                )
                                                .with("promotionRun", object()
                                                        .with("build", object()
                                                                .with("id", 999)
                                                                .with("name", "1")
                                                                .with("description", "")
                                                                .with("signature", object()
                                                                        .with("time", "2016-03-24T14:36:00Z")
                                                                        .with("user", object().with("name", "test").end())
                                                                        .end()
                                                                )
                                                                .end()
                                                        )
                                                        .with("description", "")
                                                        .with("id", 0)
                                                        .with("signature", object()
                                                                .with("time", "2016-03-24T14:36:00Z")
                                                                .with("user", object().with("name", "test").end())
                                                                .end()
                                                        )
                                                        .end()
                                                )
                                                .end()
                                        )
                                        .end()
                        )
                        .end()
                )
                .end();

        assertJsonWrite(
                expected,
                projectStatusView,
                ProjectStatusView.class
        );
    }

}
