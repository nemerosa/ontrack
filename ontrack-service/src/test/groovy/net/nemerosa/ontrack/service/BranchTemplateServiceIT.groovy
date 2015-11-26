package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.extension.api.support.TestProperty
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.service.support.template.FixedListTemplateSynchronisationSource
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class BranchTemplateServiceIT extends AbstractServiceTestSupport {

    private static final List<String> BRANCHES = ["feature/19", "feature/22", "master"]

    @Autowired
    private BranchTemplateService templateService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private AccountService accountService

    @Test
    void 'Making a branch a template'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [
                        new TemplateParameter('NAME', 'Name parameter', '')
                ],
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

    @Test(expected = AccessDeniedException)
    void 'Making a branch a template - not granted for BranchTemplateSync only'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [
                        new TemplateParameter('NAME', 'Name parameter', '')
                ],
                new ServiceConfiguration(
                        'test',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        )
        // Saves the template
        asUser().with(branch, BranchTemplateSync).call({
            templateService.setTemplateDefinition(branch.id, templateDefinition)
        })
    }

    @Test
    void 'Making a branch a template: only parameters'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [
                        new TemplateParameter('NAME', 'Name parameter', '')
                ],
                new ServiceConfiguration(
                        '',
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
    void 'Making a branch a template: only sync source'() {
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

    @Test(expected = BranchInvalidTemplateDefinitionException)
    void 'Making a branch a template: required parameters or sync source'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [],
                new ServiceConfiguration(
                        '',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        )
        // Saves the template
        asUser().with(branch, BranchTemplateMgt).call({
            templateService.setTemplateDefinition(branch.id, templateDefinition)
        })
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

    @Test
    void 'Creating a single template instance - mode auto for BranchTemplateSync'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        Branch instance = asUser().with(templateBranch, BranchTemplateSync).call {
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

    @Test
    void 'Creating a single template instance - mode manual for BranchTemplateSync'() {
        // Creates the template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template
        Branch instance = asUser().with(templateBranch, BranchTemplateSync).call {
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
    void 'Creating a single template instance - already existing and linked - for BranchTemplateSync'() {

        // Creates a template definition
        Branch templateBranch = createBranchTemplateDefinition()

        // Creates a single template - auto mode
        Branch instance = asUser().with(templateBranch, BranchTemplateSync).call {
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
    void 'Instance sync - mgt granted'() {
        doInstanceSync(BranchTemplateMgt)
    }

    @Test
    void 'Instance sync - sync granted'() {
        doInstanceSync(BranchTemplateSync)
    }

    @Test(expected = AccessDeniedException)
    void 'Instance sync - not granted'() {
        doInstanceSync(ProjectView)
    }

    protected void doInstanceSync(Class<? extends ProjectFunction> fn) {
        // Creates a template definition
        Branch template = createBranchTemplateDefinition()

        // Creates a single template
        Branch instance = asUser().with(template, BranchTemplateMgt).call {
            templateService.createTemplateInstance(
                    template.id,
                    new BranchTemplateInstanceSingleRequest(
                            'instance',
                            false, // Auto
                            [:]
                    )
            )
        }

        // Changes the template definition
        asUser().with(template, ProjectEdit).call {
            // Creates a few promotion levels
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            template,
                            nd('GOLD', 'Branch ${BRANCH} promoted to NFT.')
                    )
            )
        }

        // Re-sync the instance
        asUser().with(template, fn).call {
            assert templateService.syncInstance(instance.id).success
        }

        // Checks the new promotion level
        asUser().with(template, ProjectView).call {
            assert structureService.findPromotionLevelByName(instance.project.name, instance.name, 'GOLD').present
        }
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

    @Test(expected = AccessDeniedException)
    void 'Sync - new branches, anonymous'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()

        asUser().call {
            // Launching synchronisation
            templateService.sync(template.id)
        }

    }

    @Test
    void 'Sync - new branches, as controller'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()

        // Creating a controller user
        def account = createControllerAccount()

        asAccount(account).call {
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

    protected Account createControllerAccount() {
        asUser().with(AccountManagement).call {
            def account = accountService.create(new AccountInput(
                    TestUtils.uid("A"),
                    "Test account",
                    "test@test.com",
                    "secret",
                    Collections.<Integer> emptyList()
            ))
            // Registers as controller for the project
            accountService.saveGlobalPermission(
                    PermissionTargetType.ACCOUNT,
                    account.id(),
                    new PermissionInput('CONTROLLER')
            )
            // Loads the ACL
            account = accountService.withACL(AuthenticatedAccount.of(account))
            // OK
            account
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
    void 'Sync - existing branches, as controller'() {
        // Creating the template
        Branch template = createBranchTemplateDefinition()

        // Creating a controller user
        def account = createControllerAccount()

        // Creating a branch whose name is part of the list to sync with
        asUser().with(template, BranchCreate).call {
            structureService.newBranch(
                    Branch.of(template.project, nd('master', "Existing branch"))
            )
        }

        // Sync
        asAccount(account).call {
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
    void 'Sync - missing branches - disabled, as controller'() {
        // Creating the template with all branches
        Branch template = createBranchTemplateDefinition(BRANCHES)

        // Creating a controller user
        def account = createControllerAccount()

        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            templateService.sync(template.id)
            // Updates the sync template
            templateService.setTemplateDefinition(template.id, createTemplateDefinition(
                    BRANCHES - 'feature/22',
                    TemplateSynchronisationAbsencePolicy.DISABLE
            ))
        }

        asAccount(account).call {
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
            // Checks the branch has been deleted
            assert !structureService.findBranchByName(template.project.name, 'feature-22').present
        }

    }

    @Test
    void 'Sync - missing branches - deleted, as controller'() {
        // Creating the template with all branches
        Branch template = createBranchTemplateDefinition(BRANCHES)

        // Creating a controller user
        def account = createControllerAccount()

        asUser().with(template, BranchTemplateMgt).call {
            // Launching synchronisation, once
            templateService.sync(template.id)
            // Updates the sync template
            templateService.setTemplateDefinition(template.id, createTemplateDefinition(
                    BRANCHES - 'feature/22',
                    TemplateSynchronisationAbsencePolicy.DELETE
            ))
        }
        asAccount(account).call {
            // Relaunching sync. with one existing branch outside of the sync.
            def results = templateService.sync(template.id)
            assert results.branches.size() == 3
            assert results.branches.find { it.branchName == 'feature-19' }?.type == BranchTemplateSyncType.UPDATED
            assert results.branches.find { it.branchName == 'feature-22' }?.type == BranchTemplateSyncType.DELETED
            assert results.branches.find { it.branchName == 'master' }?.type == BranchTemplateSyncType.UPDATED
            // Checks the branch has been deleted
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
            assert results.branches.find {
                it.branchName == 'mybranch'
            }?.type == BranchTemplateSyncType.EXISTING_CLASSIC
            // Checks it is untouched
            branch = structureService.findBranchByName(template.project.name, 'mybranch').get()
            assert branch?.type == BranchType.CLASSIC
        }
    }

    @Test
    void 'Connecting a branch to a template'() {
        // Creating a template
        Branch template = createBranchTemplateDefinition([])
        // Creates a normal branch
        Branch branch = doCreateBranch(template.project, nd('mybranch', ''))
        assert branch.type == BranchType.CLASSIC
        // Populates the branch
        asUser().with(branch, ProjectEdit).call {
            // Creates a few promotion levels
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            branch,
                            nd('COPPER', 'Branch promoted to QA.')
                    )
            )
            // Creates a few validation stamps
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            nd('QA.TEST.1', 'Branch has passed the test #1')
                    )
            )
            // Creates some builds
            (1..4).each { n ->
                structureService.newBuild(
                        Build.of(
                                branch,
                                nd(n as String, "Build $n"),
                                Signature.of(Time.now(), 'user')
                        )
                )
            }
        }
        asUser().with(branch, BranchTemplateMgt).call {
            // Connects the branch to the template
            templateService.connectTemplateInstance(
                    branch.id,
                    new BranchTemplateInstanceConnectRequest(
                            template.id.value,
                            true,
                            [
                                    BRANCH: 'My branch',
                                    SCM   : 'feature/mybranch'
                            ]
                    )
            )
            // Checks the resulting branch
            branch = structureService.getBranch(branch.id)
            assert branch.name == 'mybranch'
            assert branch.description == 'Branch mybranch'
            assert branch.type == BranchType.TEMPLATE_INSTANCE
            // Checks the promotion levels
            assert structureService.findPromotionLevelByName(branch.project.name, branch.name, 'COPPER').get().description == 'Branch My branch promoted to QA.'
            assert structureService.findPromotionLevelByName(branch.project.name, branch.name, 'BRONZE').get().description == 'Branch My branch validated by QA.'
            // Checks the validation stamps
            assert structureService.findValidationStampByName(branch.project.name, branch.name, 'QA.TEST.1').get().description == 'Branch My branch has passed the test #1'
            assert structureService.findValidationStampByName(branch.project.name, branch.name, 'QA.TEST.2').get().description == 'Branch My branch has passed the test #2'
            // Checks the property
            assert propertyService.getProperty(branch, TestPropertyType).value.value == 'Value for mybranch'
        }
    }

    @Test
    void 'Connecting a branch to a template must keep existing items if not present in template'() {
        // Creating a template
        Branch template = createBranchTemplateDefinition([])
        // Creates a normal branch
        Branch branch = doCreateBranch(template.project, nd('mybranch', ''))
        assert branch.type == BranchType.CLASSIC
        // Populates the branch
        asUser().with(branch, ProjectEdit).call {
            // Creates a few promotion levels
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            branch,
                            nd('COPPER', 'Branch promoted to QA.')
                    )
            )
            // ... included one not in the template
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            branch,
                            nd('GOLD', 'Branch validated by NFT.')
                    )
            )
            // Creates a few validation stamps
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            nd('QA.TEST.1', 'Branch has passed the test #1')
                    )
            )
            // ... include one not in the template
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            nd('QA.TEST.3', 'Branch has passed the test #3')
                    )
            )
            // Creates some builds
            (1..4).each { n ->
                structureService.newBuild(
                        Build.of(
                                branch,
                                nd(n as String, "Build $n"),
                                Signature.of(Time.now(), 'user')
                        )
                )
            }
        }
        asUser().with(branch, BranchTemplateMgt).call {
            // Connects the branch to the template
            templateService.connectTemplateInstance(
                    branch.id,
                    new BranchTemplateInstanceConnectRequest(
                            template.id.value,
                            true,
                            [
                                    BRANCH: 'My branch',
                                    SCM   : 'feature/mybranch'
                            ]
                    )
            )
            // Checks the resulting branch
            branch = structureService.getBranch(branch.id)
            assert branch.name == 'mybranch'
            assert branch.description == 'Branch mybranch'
            assert branch.type == BranchType.TEMPLATE_INSTANCE
            // Checks the promotion levels
            assert structureService.findPromotionLevelByName(branch.project.name, branch.name, 'COPPER').get().description == 'Branch My branch promoted to QA.'
            assert structureService.findPromotionLevelByName(branch.project.name, branch.name, 'BRONZE').get().description == 'Branch My branch validated by QA.'
            assert structureService.findPromotionLevelByName(branch.project.name, branch.name, 'GOLD').get().description == 'Branch validated by NFT.'
            // Checks the validation stamps
            assert structureService.findValidationStampByName(branch.project.name, branch.name, 'QA.TEST.1').get().description == 'Branch My branch has passed the test #1'
            assert structureService.findValidationStampByName(branch.project.name, branch.name, 'QA.TEST.2').get().description == 'Branch My branch has passed the test #2'
            assert structureService.findValidationStampByName(branch.project.name, branch.name, 'QA.TEST.3').get().description == 'Branch has passed the test #3'
            // Checks the property
            assert propertyService.getProperty(branch, TestPropertyType).value.value == 'Value for mybranch'
        }
    }

    @Test(expected = AccessDeniedException)
    void 'Connecting a branch to a template not granted for sync only'() {
        // Creating a template
        Branch template = createBranchTemplateDefinition([])
        // Creates a normal branch
        Branch branch = doCreateBranch(template.project, nd('mybranch', ''))
        assert branch.type == BranchType.CLASSIC
        asUser().with(branch, BranchTemplateSync).call {
            // Connects the branch to the template
            templateService.connectTemplateInstance(
                    branch.id,
                    new BranchTemplateInstanceConnectRequest(
                            template.id.value,
                            true,
                            [
                                    BRANCH: 'My branch',
                                    SCM   : 'feature/mybranch'
                            ]
                    )
            )
        }
    }

    @Test(expected = BranchCannotConnectToTemplateException)
    void 'Connecting a branch to a template not possible for definitions'() {
        // Creating a template
        Branch template = createBranchTemplateDefinition([])
        Branch branch = createBranchTemplateDefinition([])
        assert branch.type == BranchType.TEMPLATE_DEFINITION
        asUser().with(branch, BranchTemplateSync).call {
            // Connects the branch to the template
            templateService.connectTemplateInstance(
                    branch.id,
                    new BranchTemplateInstanceConnectRequest(
                            template.id.value,
                            true,
                            [
                                    BRANCH: 'My branch',
                                    SCM   : 'feature/mybranch'
                            ]
                    )
            )
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
                    TestProperty.of('Value for ${sourceName}')
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
