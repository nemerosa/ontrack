package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class PredefinedPromotionLevelServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private PredefinedPromotionLevelService service

    @Test
    void 'Predefined promotion level creation'() {
        String name = uid('PVS')
        def predefinedPromotionLevel = asUser().with(GlobalSettings).call {
            service.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        assert predefinedPromotionLevel != null
        assert predefinedPromotionLevel.id.set
    }

    @Test(expected = PredefinedPromotionLevelNameAlreadyDefinedException)
    void 'Predefined promotion level name already exists'() {
        String name = uid('PVS')
        // Once --> OK
        asUser().with(GlobalSettings).call {
            service.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        // Twice --> NOK
        asUser().with(GlobalSettings).call {
            service.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined other $name"
                            )
                    )
            )
        }
    }

}