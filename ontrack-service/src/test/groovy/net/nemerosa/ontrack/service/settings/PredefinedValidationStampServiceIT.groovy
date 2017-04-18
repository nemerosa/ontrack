package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedValidationStampNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ValidationStampEdit
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class PredefinedValidationStampServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private PredefinedValidationStampService service

    @Autowired
    private StructureService structureService

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

    @Test(expected = PredefinedValidationStampNameAlreadyDefinedException)
    void 'Predefined validation stamp name already exists'() {
        String name = uid('PVS')
        // Once --> OK
        asUser().with(GlobalSettings).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        // Twice --> NOK
        asUser().with(GlobalSettings).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined other $name"
                            )
                    )
            )
        }
    }

    @Test
    void 'Bulk update of validation stamps creates a predefined validation stamp'() {
        // Creates three validation stamps, two with the same name, one with a different name
        def vs1Name = uid('VS')
        def vs2Name = uid('VS')
        def branch1 = doCreateBranch()
        def branch2 = doCreateBranch()
        def branch3 = doCreateBranch()
        def vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ''))
        def vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ''))
        def vs3 = doCreateValidationStamp(branch3, NameDescription.nd(vs2Name, ''))

        // Updates the VS1 description and image
        asUser().with(vs1, ValidationStampEdit).call {
            structureService.saveValidationStamp(
                    vs1.withDescription("My new description")
            )
            structureService.setValidationStampImage(
                    vs1.id,
                    new Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assert structureService.getValidationStamp(vs2.id).description == "My new description"
            assert structureService.getValidationStamp(vs2.id).image
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assert structureService.getValidationStamp(vs3.id).description == ""
            assert !structureService.getValidationStamp(vs3.id).image
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            def o = service.findPredefinedValidationStampByName(vs1.name)
            assert o.present
            assert o.get().description == "My new description"
            assert o.get().image
        }
    }

    @Test
    void 'Bulk update of validation stamps updates a predefined validation stamp'() {
        // Creates three validation stamps, two with the same name, one with a different name
        def vs1Name = uid('VS')
        def vs2Name = uid('VS')
        def branch1 = doCreateBranch()
        def branch2 = doCreateBranch()
        def branch3 = doCreateBranch()
        def vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ''))
        def vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ''))
        def vs3 = doCreateValidationStamp(branch3, NameDescription.nd(vs2Name, ''))
        // Predefined validation stamp
        def pvs = asAdmin().call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(NameDescription.nd(vs1Name, ''))
            )
        }

        // Updates the VS1 description and image
        asUser().with(vs1, ValidationStampEdit).call {
            structureService.saveValidationStamp(
                    vs1.withDescription("My new description")
            )
            structureService.setValidationStampImage(
                    vs1.id,
                    new Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assert structureService.getValidationStamp(vs2.id).description == "My new description"
            assert structureService.getValidationStamp(vs2.id).image
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assert structureService.getValidationStamp(vs3.id).description == ""
            assert !structureService.getValidationStamp(vs3.id).image
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            def o = service.findPredefinedValidationStampByName(vs1.name)
            assert o.present
            assert o.get().id == pvs.id
            assert o.get().description == "My new description"
            assert o.get().image
        }
    }

}