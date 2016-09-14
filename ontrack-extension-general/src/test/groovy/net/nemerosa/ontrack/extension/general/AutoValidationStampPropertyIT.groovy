package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

class AutoValidationStampPropertyIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService

    @Autowired
    private PredefinedValidationStampService predefinedValidationStampService

    @Autowired
    private PropertyService propertyService

    @Test
    void 'Get validation stamp by name - numeric'() {
        def vs = doCreateValidationStamp()
        def vss = asUser().with(vs, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(vs.branch, vs.id(), null)
        }
        assert vs == vss
    }

    @Test(expected = ValidationStampNotFoundException)
    void 'Get validation stamp by name - numeric - not found'() {
        def vs = doCreateValidationStamp()
        // Gets the VS id and deletes it, to make sure it's not there any longer
        asUser().with(vs, ProjectEdit).call {
            structureService.deleteValidationStamp(vs.id)
        }
        // Tries to create the validation stamp by ID
        asUser().with(vs, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(vs.branch, vs.id(), null)
        }
    }

    @Test
    void 'Get validation stamp by name - found'() {
        def vs = doCreateValidationStamp()
        def vss = asUser().with(vs, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(vs.branch, null, vs.name)
        }
        assert vs == vss
    }

    @Test
    void 'Get validation stamp by name - not found - predefined'() {
        def name = uid('PVS')
        def pvs = asUser().with(GlobalSettings).call {
            predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(nd(name, ''))
            )
        }
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch.project,
                    AutoValidationStampPropertyType,
                    new AutoValidationStampProperty(true)
            )
        }

        def vs = asUser().with(branch, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(branch, null, name)
        }

        assert vs != null
        assert vs.name == pvs.name
    }

    @Test(expected = ValidationStampNotFoundException)
    void 'Get validation stamp by name - not found - predefined not allowed'() {
        def name = uid('PVS')
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch.project,
                    AutoValidationStampPropertyType,
                    new AutoValidationStampProperty(false)
            )
        }

        asUser().with(branch, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(branch, null, name)
        }
    }

    @Test(expected = ValidationStampNotFoundException)
    void 'Get validation stamp by name - not found - predefined not allowed by default'() {
        def name = uid('PVS')
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(branch, null, name)
        }
    }

    @Test(expected = ValidationStampNotFoundException)
    void 'Get validation stamp by name - not found - not predefined'() {
        def name = uid('PVS')
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch.project,
                    AutoValidationStampPropertyType,
                    new AutoValidationStampProperty(true)
            )
        }

        asUser().with(branch, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(branch, null, name)
        }
    }

    @Test
    void 'Get validation stamp by name - not found - not predefined - allowing creation'() {
        def name = uid('PVS')
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch.project,
                    AutoValidationStampPropertyType,
                    new AutoValidationStampProperty(true, true)
            )
        }

        def vs = asUser().with(branch, ProjectEdit).call {
            structureService.getOrCreateValidationStamp(branch, null, name)
        }

        assert vs != null
        assert vs.name == name
        assert vs.description == "Validation automatically created on demand."
    }
}
