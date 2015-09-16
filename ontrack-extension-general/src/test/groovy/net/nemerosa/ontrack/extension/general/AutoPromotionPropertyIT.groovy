package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class AutoPromotionPropertyIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private CopyService copyService

    /**
     * This test checks that a promotion level is not attributed twice on validation upon auto promotion.
     */
    @Test
    void 'Auto promotion - only once'() {
        // Creation of a branch
        def branch = doCreateBranch()
        // Creation of validation stamps
        def vs1 = doCreateValidationStamp(branch, nd('CI.1', ''))
        def vs2 = doCreateValidationStamp(branch, nd('CI.2', ''))
        // Creation of one promotion level
        def promotionLevel = doCreatePromotionLevel(branch, nd('PL', ''))
        // Sets the auto promotion
        asUser().with(branch, ProjectConfig).call {
            propertyService.editProperty(
                    promotionLevel,
                    AutoPromotionPropertyType,
                    new AutoPromotionProperty([], 'CI.*', '')
            )
        }
        // Creates a build
        def build = doCreateBuild(branch, nd('1', ''))
        // Validates the build once
        asUser().with(branch, ProjectEdit).call {
            structureService.newValidationRun(ValidationRun.of(
                    build,
                    vs1,
                    1,
                    Signature.of('test'),
                    ValidationRunStatusID.STATUS_PASSED,
                    ''
            ))
            structureService.newValidationRun(ValidationRun.of(
                    build,
                    vs2,
                    1,
                    Signature.of('test'),
                    ValidationRunStatusID.STATUS_PASSED,
                    ''
            ))
        }
        // Checks the promotion of the build
        asUser().withView(branch).call {
            def runs = structureService.getPromotionRunsForBuild(build.id)
            assert runs*.promotionLevel.name == ['PL']
        }
        // Validates the build a second time
        asUser().with(branch, ProjectEdit).call {
            structureService.newValidationRun(ValidationRun.of(
                    build,
                    vs1,
                    1,
                    Signature.of('test'),
                    ValidationRunStatusID.STATUS_PASSED,
                    ''
            ))
        }
        // Checks the promotion of the build (only one)
        asUser().withView(branch).call {
            def runs = structureService.getPromotionRunsForBuild(build.id)
            assert runs*.promotionLevel.name == ['PL']
        }
    }

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
                    new AutoPromotionProperty([vs1, vs2], '', '')
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

    /**
     * Regression test for #290
     */
    @Test
    void 'Branch cloning with auto promotion'() {
        // Creates a promotion level
        PromotionLevel promotionLevel = doCreatePromotionLevel()
        def branch = promotionLevel.branch
        // Creates a validation stamp
        def vs1 = doCreateValidationStamp(branch, nd('VS1', ''))
        doCreateValidationStamp(branch, nd('VS2', ''))
        // Auto promotion
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    promotionLevel,
                    AutoPromotionPropertyType,
                    new AutoPromotionProperty([vs1], '', '')
            )
        }
        // Cloning the branch
        String clonedBranchName = TestUtils.uid("B")
        Branch clonedBranch = asUser().with(branch, ProjectEdit).call {
            copyService.cloneBranch(
                    branch,
                    new BranchCloneRequest(
                            clonedBranchName,
                            []
                    )
            )
        }
        assert clonedBranch.name == clonedBranchName

        // Gets the cloned promotion level
        def clonedPromotionLevel = asUser().with(clonedBranch, ProjectView).call {
            structureService.findPromotionLevelByName(
                    clonedBranch.project.name,
                    clonedBranch.name,
                    promotionLevel.name
            ).get()
        }

        // Gets the auto validation property for the cloned branch
        def property = asUser().with(clonedPromotionLevel, ProjectView).call {
            propertyService.getProperty(clonedPromotionLevel, AutoPromotionPropertyType)
        }
        assert property != null
        assert !property.empty
        assert property.value != null
        assert property.value.validationStamps.collect { it.name } == ['VS1']
    }
}
