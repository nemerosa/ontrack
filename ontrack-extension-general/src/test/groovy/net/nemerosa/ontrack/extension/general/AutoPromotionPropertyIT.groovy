package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class AutoPromotionPropertyIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    /**
     * This test checks that whenever a validation stamp, which was part of an auto promotion configuration,
     * is deleted, it is automatically removed from the auto promotion configuration.
     */
    @Test
    void 'Auto promotion - auto configuration on validation stamp deletion'() {
        // Creation of a branch
        def branch = doCreateBranch()
        // Creation of two validation stamps
        def vs1 = doCreateValidationStamp(branch, nd('VS1', ''))
        def vs2 = doCreateValidationStamp(branch, nd('VS2', ''))
        // Creation of one promotion level
        def promotionLevel = doCreatePromotionLevel(branch, nd('PL', ''))
        // Sets the auto promotion
        asUser().with(branch, ProjectConfig).call {
            propertyService.editProperty(
                    promotionLevel,
                    AutoPromotionPropertyType,
                    new AutoPromotionProperty([vs1, vs2])
            )
        }
        // Deletes a validation stamp
        asUser().with(branch, ProjectEdit).with(branch, ProjectEdit).call {
            structureService.deleteValidationStamp(vs1.id)
        }
        // Gets the auto promotion configuration
        AutoPromotionProperty property = asUser().with(branch, ProjectView).call {
            propertyService.getProperty(promotionLevel, AutoPromotionPropertyType).value
        }
        // Checks it does not contain the VS1 any longer
        assert property.validationStamps.collect { it.name } == ['VS2']
    }
}
