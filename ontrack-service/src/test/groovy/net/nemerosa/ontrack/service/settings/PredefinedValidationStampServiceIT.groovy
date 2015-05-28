package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class PredefinedValidationStampServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private PredefinedValidationStampService service

    @Test
    void 'Predefined validation stamp creation'() {
        String name = uid('PVS')
        def predefinedValidationStamp = asUser().with(GlobalSettings).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        assert predefinedValidationStamp != null
        assert predefinedValidationStamp.id.set
    }

}