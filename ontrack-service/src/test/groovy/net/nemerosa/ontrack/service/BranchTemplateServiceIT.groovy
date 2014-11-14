package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.security.BranchCreate
import net.nemerosa.ontrack.model.security.BranchTemplateMgt
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.service.support.property.TestProperty
import net.nemerosa.ontrack.service.support.property.TestPropertyType
import net.nemerosa.ontrack.service.support.template.FixedListTemplateSynchronisationSource
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class BranchTemplateServiceIT extends AbstractServiceTestSupport {

    private static final List<String> BRANCHES = ["feature/19", "feature/22", "master"]

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
                                    'BRANCH' : 'INSTANCE',
                                    'SCM'    : 'master',
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
                                    'BRANCH': 'INSTANCE',
                                    'SCM'   : 'master'
                            ]
                    )
            )
        }

        // Checks the created branch
        checkBranchTemplateInstance(instance)
    }

    @Test(expected = BranchClassicCannotBeTemplateInstanceException)
    void 'Creating a single template instance - already existing - classic'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()
        // Creates an existing branch
        doCreateBranch(
                templateBranch.project,
                nd('classic', "Normal branch")
        )

        // Creates a single template
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'classic',
                            false, // Auto
                            [:]
                    )
            )
        }
    }

    @Test(expected = BranchTemplateDefinitionCannotBeTemplateInstanceException)
    void 'Creating a single template instance - already existing - definition'() {
        // Creates a template definition
        Branch templateBranch = createBranchTemplateDefinition()
        // Creates another template definition on the same project
        createBranchTemplateDefinition(templateBranch.project, 'anotherTemplate')

        // Creates a single template
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'anotherTemplate',
                            false, // Auto
                            [:]
                    )
            )
        }
    }

    @Test(expected = BranchTemplateInstanceCannotUpdateBasedOnOtherDefinitionException)
    void 'Creating a single template instance - already existing - another definition'() {

        // Creates a template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates another template definition on the same project
        Branch anotherTemplateBranch = createBranchTemplateDefinition(templateBranch.project, 'anotherTemplate')

        // Creates an instance based on this other template
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    anotherTemplateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            false, // Auto
                            [:]
                    )
            )
        }

        // Tries to update this instance
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            false, // Auto
                            [:]
                    )
            )
        }
    }

    @Test
    void 'Creating a single template instance - already existing and linked'() {

        // Creates a template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template - auto mode
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

        // Updates this branch
        asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    templateBranch.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            true, // Manual
                            [
                                    'BRANCH': 'Updated instance',
                                    'SCM'   : 'instance'
                            ]
                    )
            )
        }

        // Checks the updated branch
        checkBranchTemplateInstance(instance, 'Updated instance')

    }

    @Test
    void 'Sync - new branches'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()

        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            BRANCHES.each { sourceName ->
                def branchName = sourceName.replace('/', '-')
                assert results.branches.find { it.branchName == branchName }?.type == BranchTemplateSyncType.CREATED
            }

            // Checks the branches have been created
            BRANCHES.each { sourceName ->
                def branchName = sourceName.replace('/', '-')
                def branch = structureService.findBranchByName(template.project.name, branchName)
                assert branch.present
                // Gets its template instance
                def instanceOpt = templateService.getTemplateInstance(branch.get().id)
                assert instanceOpt.present
                def instance = instanceOpt.get()
                assert instance.templateDefinitionId == template.id
                assert instance.parameterValues == [
                        new TemplateParameterValue('BRANCH', branchName.toUpperCase()),
                        new TemplateParameterValue('SCM', sourceName)
                ]
            }
        }

    }

    @Test
    void 'Sync - existing branches'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()
        // Creating a branch whose name is part of the list to sync with
        asUser().with(template, BranchCreate).call {
            structureService.newBranch(
                    Branch.of(template.project, nd('master', "Existing branch"))
            )
        }
        // Sync
        asUser().with(template, BranchTemplateMgt).call {
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.CREATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.CREATED
            assert results.branches.find { it.branchName == 'master' }?.type == BranchTemplateSyncType.EXISTING_CLASSIC
        }
    }

    @Test
    void 'Sync - existing definition'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()
        // Creating a branch whose name is part of the list to sync with
        def existing = asUser().with(template, BranchCreate).call {
            structureService.newBranch(
                    Branch.of(template.project, nd('master', "Existing branch"))
            )
        }
        // ... and is itself a definition
        asUser().with(template, BranchTemplateMgt).call {
            templateService.setTemplateDefinition(
                    existing.id,
                    createTemplateDefinition(BRANCHES, TemplateSynchronisationAbsencePolicy.DISABLE)
            )
        }
        // Sync
        asUser().with(template, BranchTemplateMgt).call {
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.CREATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.CREATED
            assert results.branches.find {
                it.branchName == 'master'
            }?.type == BranchTemplateSyncType.EXISTING_DEFINITION
        }
    }

    @Test
    void 'Sync - existing instance'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()
        // Creates another template
        Branch anotherTemplate = createBranchTemplateDefinition(template.project, 'anotherTemplate', ['master'])
        // Sync this other template
        asUser().with(anotherTemplate, BranchTemplateMgt).call {
            def results = templateService.sync(anotherTemplate.id)
            assert results.branches.size() == 1
            assert results.branches.find { it.branchName == 'master' }?.type == BranchTemplateSyncType.CREATED
        }
        // Sync
        asUser().with(template, BranchTemplateMgt).call {
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.CREATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.CREATED
            results.branches.find { it.branchName == 'master' } with {
                assert type == BranchTemplateSyncType.EXISTING_INSTANCE_FROM_OTHER
                assert otherTemplateName == 'anotherTemplate'
            }
        }
    }

    @Test
    void 'Sync - update branches'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()

        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            templateService.sync(template.id)
            // ... twice
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            BRANCHES.each { sourceName ->
                def branchName = sourceName.replace('/', '-')
                assert results.branches.find { it.branchName == branchName }?.type == BranchTemplateSyncType.UPDATED
            }
        }

    }

    @Test
    void 'Sync - removing parameters after and before sync'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()
        // First sync
        asUser().with(template, BranchTemplateMgt).call {
            templateService.sync(template.id)
        }
        // Getting the branch template instance
        TemplateInstance instance = asUser().with(template, BranchTemplateMgt).call {
            templateService.getTemplateInstance(
                    structureService.findBranchByName(template.project.name, 'master').get().id
            )
        }.get()
        // Checks it has two parameters
        assert instance.parameterValues == [
                new TemplateParameterValue('BRANCH', 'MASTER'),
                new TemplateParameterValue('SCM', 'master')
        ]
        // Updates the template definition and syncs again
        TemplateDefinition definition = createTemplateDefinition(BRANCHES, TemplateSynchronisationAbsencePolicy.DISABLE)
        definition = new TemplateDefinition(
                [
                        new TemplateParameter(
                                'BRANCH',
                                "Display name for the branch",
                                'R_${sourceName.toUpperCase().replaceAll("/","_")}'
                        )
                ],
                definition.synchronisationSourceConfig,
                definition.absencePolicy,
                definition.interval
        )
        asUser().with(template, BranchTemplateMgt).call {
            templateService.setTemplateDefinition(template.id, definition)
            templateService.sync(template.id)
        }
        // Checks the template instance has now one parameter only
        instance = asUser().with(template, BranchTemplateMgt).call {
            templateService.getTemplateInstance(
                    structureService.findBranchByName(template.project.name, 'master').get().id
            )
        }.get()
        assert instance.parameterValues == [
                new TemplateParameterValue('BRANCH', 'R_MASTER')
        ]
    }

    @Test
    void 'Sync - missing branches - disabled'() {
        // Creating the template with all branches
        Branch template = createBranchTemplateDefinition(BRANCHES)
        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            templateService.sync(template.id)
            // Updates the sync template
            templateService.setTemplateDefinition(template.id, createTemplateDefinition(
                    BRANCHES - 'feature/22',
                    TemplateSynchronisationAbsencePolicy.DISABLE
            ))
            // Relaunching sync. with one existing branch outside of the sync.
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.UPDATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.DISABLED
            assert results.branches.find { it.branchName == 'master' }?.type == BranchTemplateSyncType.UPDATED
            // Checks the branch has been disabled
            assert structureService.findBranchByName(template.project.name, 'feature-22').present
            assert structureService.findBranchByName(template.project.name, 'feature-22').get().disabled
        }

    }

    @Test
    void 'Sync - missing branches - deleted'() {
        // Creating the template with all branches
        Branch template = createBranchTemplateDefinition(BRANCHES)
        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            templateService.sync(template.id)
            // Updates the sync template
            templateService.setTemplateDefinition(template.id, createTemplateDefinition(
                    BRANCHES - 'feature/22',
                    TemplateSynchronisationAbsencePolicy.DELETE
            ))
            // Relaunching sync. with one existing branch outside of the sync.
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.UPDATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.DELETED
            assert results.branches.find { it.branchName == 'master' }?.type == BranchTemplateSyncType.UPDATED
            // Checks the branch has been disabled
            assert !structureService.findBranchByName(template.project.name, 'feature-22').present
        }

    }

    @Test
    void 'Sync after disconnect must not update the disconnected branch'() {
        Branch template = createBranchTemplateDefinition(['mybranch'])
        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            def results = templateService.sync(template.id)
            assert results.branches.find { it.branchName == 'mybranch' }?.type == BranchTemplateSyncType.CREATED
            // Gets the branch instance
            def branch = structureService.findBranchByName(template.project.name, 'mybranch').get()
            assert branch?.type == BranchType.TEMPLATE_INSTANCE
            // Disconnects it
            branch = templateService.disconnectTemplateInstance(branch.id)
            assert branch?.type == BranchType.CLASSIC
            // Relaunches sync
            results = templateService.sync(template.id)
            assert results.branches.find { it.branchName == 'mybranch' }?.type == BranchTemplateSyncType.EXISTING_CLASSIC
            // Checks it is untouched
            branch = structureService.findBranchByName(template.project.name, 'mybranch').get()
            assert branch?.type == BranchType.CLASSIC
        }
    }

    protected void checkBranchTemplateInstance(Branch instance) {
        checkBranchTemplateInstance(instance, 'INSTANCE')
    }

    protected void checkBranchTemplateInstance(Branch instance, String branch) {
        assert instance?.type == BranchType.TEMPLATE_INSTANCE
        assert instance.description == 'Branch instance'

        // Checks the branch properties
        def property = propertyService.getProperty(instance, TestPropertyType)
        assert !property.empty
        assert property.value.value == 'Value for instance'

        // Checks the branch promotion levels
        asUser().withView(instance).call {
            def copper = structureService.findPromotionLevelByName(instance.project.name, instance.name, 'COPPER')
            assert copper.present
            assert copper.get().description == "Branch ${branch} promoted to QA."
            def bronze = structureService.findPromotionLevelByName(instance.project.name, instance.name, 'BRONZE')
            assert bronze.present
            assert bronze.get().description == "Branch ${branch} validated by QA."
        }

        // Checks the branch validation stamps
        asUser().withView(instance).call {
            def test1 = structureService.findValidationStampByName(instance.project.name, instance.name, 'QA.TEST.1')
            assert test1.present
            assert test1.get().description == "Branch ${branch} has passed the test #1"
            def test2 = structureService.findValidationStampByName(instance.project.name, instance.name, 'QA.TEST.2')
            assert test2.present
            assert test2.get().description == "Branch ${branch} has passed the test #2"
        }
    }

    protected Branch createBranchTemplateDefinition() {
        createBranchTemplateDefinition(doCreateProject(), 'template', BRANCHES)
    }

    protected Branch createBranchTemplateDefinition(Collection<String> sourceNames) {
        createBranchTemplateDefinition(doCreateProject(), 'template', sourceNames)
    }

    protected Branch createBranchTemplateDefinition(Project project, String templateName) {
        createBranchTemplateDefinition(project, templateName, BRANCHES)
    }

    protected Branch createBranchTemplateDefinition(Project project, String templateName, Collection<String> sourceNames) {
        // Creates the base branch
        Branch templateBranch = doCreateBranch(
                project,
                nd(templateName, 'Branch ${sourceName}')
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
                    new TestProperty('Value for ${sourceName}')
            )
        }

        // Template definition
        TemplateDefinition templateDefinition = createTemplateDefinition(sourceNames, TemplateSynchronisationAbsencePolicy.DELETE)
        // Saves the template
        templateBranch = asUser().with(templateBranch, BranchTemplateMgt).call {
            templateService.setTemplateDefinition(templateBranch.id, templateDefinition)
        }
        templateBranch
    }

    protected
    static TemplateDefinition createTemplateDefinition(Collection<String> sourceNames, TemplateSynchronisationAbsencePolicy absencePolicy) {
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [
                        new TemplateParameter(
                                'BRANCH',
                                "Display name for the branch",
                                '${sourceName.toUpperCase().replaceAll("/","-")}'
                        ),
                        new TemplateParameter(
                                'SCM',
                                "SCM branch",
                                '${sourceName}'
                        )
                ],
                new ServiceConfiguration(
                        FixedListTemplateSynchronisationSource.ID,
                        JsonUtils.object()
                                .with("names", JsonUtils.stringArray(sourceNames))
                                .end()
                ),
                absencePolicy,
                10
        )
        templateDefinition
    }

}
