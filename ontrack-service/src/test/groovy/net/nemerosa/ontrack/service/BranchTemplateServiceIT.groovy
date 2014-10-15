package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.BranchTemplateInstanceMissingParametersException
import net.nemerosa.ontrack.model.exceptions.BranchTemplateInstanceUnknownParametersException
import net.nemerosa.ontrack.model.security.BranchTemplateMgt
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.service.support.property.TestProperty
import net.nemerosa.ontrack.service.support.property.TestPropertyType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class BranchTemplateServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private BranchTemplateService templateService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    @Test
    void 'Making a branch a template'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [],
                new ServiceConfiguration(
                        'test',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        )
        // Saves the template
        Branch savedBranch = asUser().with(branch, BranchTemplateMgt).call({
            templateService.setTemplateDefinition(branch.id, templateDefinition)
        })
        // Checks
        assert savedBranch.id == branch.id
        assert savedBranch.type == BranchType.TEMPLATE_DEFINITION
    }

    @Test
    void 'Creating a single template instance - mode auto'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        Branch instance = asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            false, // Auto
                            [:]
                    )
            )
        }

        // Checks the created branch
        checkBranchTemplateInstance(instance)
    }

    @Test(expected = BranchTemplateInstanceMissingParametersException)
    void 'Creating a single template instance - mode manual - missing parameters'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            true, // Manual
                            [:]
                    )
            )
        }
    }

    @Test(expected = BranchTemplateInstanceUnknownParametersException)
    void 'Creating a single template instance - mode manual - unknown parameters'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            true, // Manual
                            [
                                    'BRANCH': 'INSTANCE',
                                    'unknown': "Test"
                            ]
                    )
            )
        }
    }

    @Test
    void 'Creating a single template instance - mode manual'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        Branch instance = asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            true, // Manual
                            [
                                    'BRANCH': 'INSTANCE'
                            ]
                    )
            )
        }

        // Checks the created branch
        checkBranchTemplateInstance(instance)
    }

    protected void checkBranchTemplateInstance(Branch instance) {
        assert instance.type == BranchType.TEMPLATE_INSTANCE
        assert instance.description == 'Branch instance'

        // Checks the branch properties
        def property = propertyService.getProperty(instance, TestPropertyType)
        assert !property.empty
        assert property.value.value == 'Value for instance'

        // Checks the branch promotion levels
        asUser().withView(instance).call {
            def copper = structureService.findPromotionLevelByName(instance.project.name, instance.name, 'COPPER')
            assert copper.present
            assert copper.get().description == 'Branch INSTANCE promoted to QA.'
            def bronze = structureService.findPromotionLevelByName(instance.project.name, instance.name, 'BRONZE')
            assert bronze.present
            assert bronze.get().description == 'Branch INSTANCE validated by QA.'
        }

        // Checks the branch validation stamps
        asUser().withView(instance).call {
            def test1 = structureService.findValidationStampByName(instance.project.name, instance.name, 'QA.TEST.1')
            assert test1.present
            assert test1.get().description == 'Branch INSTANCE has passed the test #1'
            def test2 = structureService.findValidationStampByName(instance.project.name, instance.name, 'QA.TEST.2')
            assert test2.present
            assert test2.get().description == 'Branch INSTANCE has passed the test #2'
        }
    }

    protected Branch createBranchTemplateDefinition() {
        // Creates the base branch
        Branch templateBranch = doCreateBranch(
                doCreateProject(),
                nd('template', 'Branch ${branchName}')
        );

        asUser().with(templateBranch, ProjectEdit).call {
            // Creates a few promotion levels
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            templateBranch,
                            nd('COPPER', 'Branch ${BRANCH} promoted to QA.')
                    )
            )
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            templateBranch,
                            nd('BRONZE', 'Branch ${BRANCH} validated by QA.')
                    )
            )
            // Creates a few validation stamps
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            templateBranch,
                            nd('QA.TEST.1', 'Branch ${BRANCH} has passed the test #1')
                    )
            )
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            templateBranch,
                            nd('QA.TEST.2', 'Branch ${BRANCH} has passed the test #2')
                    )
            )
            // Creates a property
            propertyService.editProperty(
                    templateBranch,
                    TestPropertyType,
                    new TestProperty('Value for ${branchName}')
            )
        }

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [
                        new TemplateParameter(
                                'BRANCH',
                                "Display name for the branch",
                                '${branchName.toUpperCase()}'
                        )
                ],
                new ServiceConfiguration(
                        'test',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        )
        // Saves the template
        templateBranch = asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.setTemplateDefinition(templateBranch.id, templateDefinition)
        }
        templateBranch
    }

}
