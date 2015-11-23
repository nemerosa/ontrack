package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.general.MessagePropertyType
import net.nemerosa.ontrack.extension.general.MessageType
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.PromotionRunRequest
import net.nemerosa.ontrack.model.structure.PropertyCreationRequest
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class PromotionRunControllerIT extends AbstractWebTestSupport {

    @Autowired
    private PromotionRunController controller

    @Autowired
    private PropertyService propertyService

    @Test
    void 'New promotion run'() {
        // Promotion level
        def promotionLevel = doCreatePromotionLevel()
        // Build
        def build = doCreateBuild(
                promotionLevel.branch,
                nd('1', "Build 1")
        )
        // Promotion run request
        PromotionRunRequest request = new PromotionRunRequest(
                promotionLevel.id(),
                '',
                Time.now(),
                "Run",
                []
        )
        // Call
        def run = asUser().with(promotionLevel, ProjectEdit).call {
            controller.newPromotionRun(build.id, request)
        }
        // Checks
        assert run != null
    }

    @Test
    void 'New promotion run with properties'() {
        // Promotion level
        def promotionLevel = doCreatePromotionLevel()
        // Build
        def build = doCreateBuild(
                promotionLevel.branch,
                nd('1', "Build 1")
        )
        // Promotion run request
        PromotionRunRequest request = new PromotionRunRequest(
                promotionLevel.id(),
                '',
                Time.now(),
                "Run",
                [
                        new PropertyCreationRequest(
                                MessagePropertyType.class.name,
                                JsonUtils.object()
                                        .with('type', 'INFO')
                                        .with('text', 'Message')
                                        .end()
                        )
                ]
        )
        def run = asUser().with(promotionLevel, ProjectEdit).call {
            controller.newPromotionRun(build.id, request)
        }
        // Checks
        assert run != null
        def property = propertyService.getProperty(run, MessagePropertyType)
        assert !property.empty
        assert property.value.type == MessageType.INFO
        assert property.value.text == 'Message'
    }

}