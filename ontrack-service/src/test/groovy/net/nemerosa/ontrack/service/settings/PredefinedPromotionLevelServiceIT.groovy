package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.PromotionLevelEdit
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.test.TestUtils
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

    @Test
    void 'Bulk update of promotion levels creates a predefined promotion level'() {
        // Creates three promotion levels, two with the same name, one with a different name
        def pl1Name = uid('PL')
        def pl2Name = uid('PL')
        def branch1 = doCreateBranch()
        def branch2 = doCreateBranch()
        def branch3 = doCreateBranch()
        def pl1 = doCreatePromotionLevel(branch1, NameDescription.nd(pl1Name, ''))
        def pl2 = doCreatePromotionLevel(branch2, NameDescription.nd(pl1Name, ''))
        def pl3 = doCreatePromotionLevel(branch3, NameDescription.nd(pl2Name, ''))

        // Updates the PL1 description and image
        asUser().with(pl1, PromotionLevelEdit).call {
            structureService.savePromotionLevel(
                    pl1.withDescription("My new description")
            )
            structureService.setPromotionLevelImage(
                    pl1.id,
                    new Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings).call {
            structureService.bulkUpdatePromotionLevels(pl1.id)
        }

        // Checks the promotion level with same name has been updated
        asAdmin().call {
            assert structureService.getPromotionLevel(pl2.id).description == "My new description"
            assert structureService.getPromotionLevel(pl2.id).image
        }

        // Checks the promotion level with NOT same name has NOT been updated
        asAdmin().call {
            assert structureService.getPromotionLevel(pl3.id).description == ""
            assert !structureService.getPromotionLevel(pl3.id).image
        }

        // Checks the predefined promotion level has been created
        asAdmin().call {
            def o = service.findPredefinedPromotionLevelByName(pl1.name)
            assert o.present
            assert o.get().description == "My new description"
            assert o.get().image
        }
    }

    @Test
    void 'Bulk update of promotion levels updates a predefined promotion level'() {
        // Creates three promotion levels, two with the same name, one with a different name
        def pl1Name = uid('PL')
        def pl2Name = uid('PL')
        def branch1 = doCreateBranch()
        def branch2 = doCreateBranch()
        def branch3 = doCreateBranch()
        def pl1 = doCreatePromotionLevel(branch1, NameDescription.nd(pl1Name, ''))
        def pl2 = doCreatePromotionLevel(branch2, NameDescription.nd(pl1Name, ''))
        def pl3 = doCreatePromotionLevel(branch3, NameDescription.nd(pl2Name, ''))
        // Predefined promotion level
        def ppl = asAdmin().call {
            service.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(NameDescription.nd(pl1Name, ''))
            )
        }

        // Updates the PL1 description and image
        asUser().with(pl1, PromotionLevelEdit).call {
            structureService.savePromotionLevel(
                    pl1.withDescription("My new description")
            )
            structureService.setPromotionLevelImage(
                    pl1.id,
                    new Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings).call {
            structureService.bulkUpdatePromotionLevels(pl1.id)
        }

        // Checks the promotion level with same name has been updated
        asAdmin().call {
            assert structureService.getPromotionLevel(pl2.id).description == "My new description"
            assert structureService.getPromotionLevel(pl2.id).image
        }

        // Checks the promotion level with NOT same name has NOT been updated
        asAdmin().call {
            assert structureService.getPromotionLevel(pl3.id).description == ""
            assert !structureService.getPromotionLevel(pl3.id).image
        }

        // Checks the predefined promotion level has been created
        asAdmin().call {
            def o = service.findPredefinedPromotionLevelByName(pl1.name)
            assert o.present
            assert o.get().id == ppl.id
            assert o.get().description == "My new description"
            assert o.get().image
        }
    }

}