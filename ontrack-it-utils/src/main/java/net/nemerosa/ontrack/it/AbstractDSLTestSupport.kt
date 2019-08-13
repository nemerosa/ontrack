package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.buildfilter.StandardFilterProviderDataBuilder
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.labels.*
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass
import kotlin.test.assertEquals

abstract class AbstractDSLTestSupport : AbstractServiceTestSupport() {

    @Autowired
    protected lateinit var securityService: SecurityService

    @Autowired
    protected lateinit var ontrackConfigProperties: OntrackConfigProperties

    @Autowired
    protected lateinit var labelManagementService: LabelManagementService

    @Autowired
    protected lateinit var projectLabelManagementService: ProjectLabelManagementService

    @Autowired
    protected lateinit var buildFilterService: BuildFilterService

    @Autowired
    protected lateinit var settingsManagerService: SettingsManagerService

    @Autowired
    protected lateinit var settingsService: CachedSettingsService

    @Autowired
    protected lateinit var predefinedPromotionLevelService: PredefinedPromotionLevelService

    @Autowired
    protected lateinit var predefinedValidationStampService: PredefinedValidationStampService

    /**
     * Kotlin friendly
     */
    fun asUserWithView(vararg entities: ProjectEntity, code: () -> Unit) {
        asUserWithView(*entities).execute(code)
    }

    /**
     * Kotlin friendly account role execution
     */
    fun ProjectEntity.asAccountWithProjectRole(role: String, code: () -> Unit) {
        val account = doCreateAccountWithProjectRole(project, role)
        asAccount(account).execute(code)
    }

    /**
     * Kotlin friendly account role execution
     */
    fun asAccountWithGlobalRole(role: String, code: () -> Unit) {
        val account = doCreateAccountWithGlobalRole(role)
        asAccount(account).execute(code)
    }

    fun <T> withDisabledConfigurationTest(code: () -> T): T {
        val configurationTest = ontrackConfigProperties.isConfigurationTest
        ontrackConfigProperties.isConfigurationTest = false
        return try {
            code()
        } finally {
            ontrackConfigProperties.isConfigurationTest = configurationTest
        }
    }

    fun project(init: Project.() -> Unit = {}): Project {
        val project = doCreateProject()
        securityService.asAdmin {
            project.init()
        }
        return project
    }

    fun <T> project(init: Project.() -> T): T {
        val project = doCreateProject()
        return asAdmin().call {
            project.init()
        }
    }

    fun Project.branch(name: String = uid("B"), init: Branch.() -> Unit = {}): Branch {
        val branch = doCreateBranch(this, NameDescription.nd(name, ""))
        branch.init()
        return branch
    }

    fun <T> Project.branch(name: String = uid("B"), init: Branch.() -> T): T {
        val branch = doCreateBranch(this, NameDescription.nd(name, ""))
        return branch.init()
    }

    fun Branch.promotionLevel(name: String = uid("P"), init: PromotionLevel.() -> Unit = {}): PromotionLevel =
            doCreatePromotionLevel(this, NameDescription.nd(name, "")).apply {
                init()
            }

    /**
     * Creates and returns a validation stamp
     *
     * @receiver Branch to create a validation stamp for
     * @param name Name of the validation stamp to create
     * @return Created validation stamp
     */
    fun Branch.validationStamp(
            name: String = uid("VS"),
            validationDataTypeConfig: ValidationDataTypeConfig<*>? = null
    ): ValidationStamp =
            doCreateValidationStamp(this, NameDescription.nd(name, ""), validationDataTypeConfig)

    fun Branch.build(name: String = uid("B"), init: (Build.() -> Unit)? = {}): Build {
        val build = doCreateBuild(this, NameDescription.nd(name, ""))
        if (init != null) {
            build.init()
        }
        return build
    }

    fun <T> Branch.build(name: String, init: Build.() -> T): T {
        val build = doCreateBuild(this, NameDescription.nd(name, ""))
        return build.init()
    }

    protected fun <T, P : PropertyType<T>> Build.property(type: KClass<P>, value: T) {
        propertyService.editProperty<T>(
                this,
                type.java,
                value
        )
    }

    fun Build.promote(promotionLevel: PromotionLevel) {
        doPromote(this, promotionLevel, "")
    }

    /**
     * Creates a validation run on a build.
     *
     * @receiver Build to validate
     * @param validationStamp Stamp to apply
     * @param validationRunStatusID Status to apply
     */
    fun Build.validate(
            validationStamp: ValidationStamp,
            validationRunStatusID: ValidationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
            description: String? = null
    ): ValidationRun {
        return this.validateWithData<Any>(
                validationStampName = validationStamp.name,
                validationRunStatusID = validationRunStatusID,
                description = description
        )
    }

    /**
     * Creates a validation run on a build, possibly with some data and a status.
     */
    fun <T> Build.validateWithData(
            validationStamp: ValidationStamp,
            validationRunStatusID: ValidationRunStatusID? = null,
            validationDataTypeId: String? = null,
            validationRunData: T? = null,
            description: String? = null
    ) = validateWithData(
            validationStampName = validationStamp.name,
            validationRunStatusID = validationRunStatusID,
            validationDataTypeId = validationDataTypeId,
            validationRunData = validationRunData,
            description = description
    )

    /**
     * Creates a validation run on a build, possibly with some data and a status.
     */
    fun <T> Build.validateWithData(
            validationStampName: String,
            validationRunStatusID: ValidationRunStatusID? = null,
            validationDataTypeId: String? = null,
            validationRunData: T? = null,
            description: String? = null
    ): ValidationRun {
        return asUser().withView(this).with(this, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    this,
                    ValidationRunRequest(
                            validationStampName = validationStampName,
                            dataTypeId = validationDataTypeId,
                            data = validationRunData,
                            validationRunStatusId = validationRunStatusID,
                            description = description
                    )
            )
        }
    }

    fun Build.linkTo(project: Project, buildName: String) {
        val build = structureService.buildSearch(
                project.id,
                BuildSearchForm().withBuildExactMatch(true).withBuildName(buildName)
        ).firstOrNull() ?: throw BuildNotFoundException(project.name, buildName)
        linkTo(build)
    }

    fun Build.linkTo(build: Build) {
        structureService.addBuildLink(
                this,
                build
        )
    }

    /**
     * Change of status for a validation run
     */
    fun ValidationRun.validationStatus(status: ValidationRunStatusID, description: String) {
        asUser().with(this, ValidationRunStatusChange::class.java).execute {
            structureService.newValidationRunStatus(
                    this,
                    ValidationRunStatus.of(
                            Signature.of("test"),
                            status,
                            description
                    )
            )
        }
    }

    /**
     * Creates a label
     */
    fun label(category: String? = uid("C"), name: String = uid("N"), checkForExisting: Boolean = true): Label {
        return asUser().with(LabelManagement::class.java).call {
            if (checkForExisting) {
                val labels = labelManagementService.findLabels(category, name)
                val existingLabel = labels.firstOrNull()
                existingLabel ?: labelManagementService.newLabel(
                        LabelForm(
                                category = category,
                                name = name,
                                description = null,
                                color = "#FF0000"
                        )
                )
            } else {
                labelManagementService.newLabel(
                    LabelForm(
                            category = category,
                            name = name,
                            description = null,
                            color = "#FF0000"
                    )
                )
            }
        }
    }

    /**
     * Sets some labels to a project
     */
    var Project.labels: List<Label>
        get() = asUserWithView(this).call {
            projectLabelManagementService.getLabelsForProject(this)
        }
        set(value) {
            asUser().with(this, ProjectLabelManagement::class.java).execute {
                projectLabelManagementService.associateProjectToLabels(
                        this,
                        ProjectLabelForm(
                                value.map { it.id }
                        )
                )
            }
        }

    /**
     * Creation of a predefined promotion level
     */
    protected fun predefinedPromotionLevel(name: String, description: String = "", image: Boolean = false) {
        asAdmin().call {
            val ppl = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                            NameDescription.nd(name, description)
                    )
            )
            if (image) {
                val document = Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
                predefinedPromotionLevelService.setPredefinedPromotionLevelImage(
                        ppl.id,
                        document
                )
            }
        }
    }

    /**
     * Creation of a predefined validation stamp
     */
    protected fun predefinedValidationStamp(name: String, description: String = "", image: Boolean = false) {
        asAdmin().call {
            val pps = predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(name, description)
                    )
            )
            if (image) {
                val document = Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
                predefinedValidationStampService.setPredefinedValidationStampImage(
                        pps.id,
                        document
                )
            }
        }
    }

    /**
     * Saving current settings, runs some code and restores the format settings
     */
    private inline fun <reified T> withSettings(code: () -> Unit) {
        val settings: T = settingsService.getCachedSettings(T::class.java)
        // Runs the code
        code()
        // Restores the initial settings (only in case of success)
        asAdmin().execute {
            settingsManagerService.saveSettings(settings)
        }
    }

    /**
     * Saving current "main build links" settings, runs some code and restores the format settings
     */
    protected fun withMainBuildLinksSettings(code: () -> Unit) = withSettings<MainBuildLinksConfig>(code)

    /**
     * Settings "main build links" settings
     */
    protected fun setMainBuildLinksSettings(vararg labels: String) {
        asAdmin().execute {
            settingsManagerService.saveSettings(
                    MainBuildLinksConfig(
                            labels.toList()
                    )
            )
        }
    }

    /**
     * Getting "main build links" settings
     */
    protected val mainBuildLinksSettings: List<String>
        get() = settingsService.getCachedSettings(MainBuildLinksConfig::class.java).labels

    protected fun Branch.assertBuildSearch(filterBuilder: (StandardFilterProviderDataBuilder) -> Unit): BuildSearchAssertion {
        val data = buildFilterService.standardFilterProviderData(10)
        filterBuilder(data)
        val filter = data.build()
        return BuildSearchAssertion(this, filter)
    }

    protected class BuildSearchAssertion(
            private val branch: Branch,
            private val filter: BuildFilterProviderData<*>
    ) {
        infix fun returns(expected: Build) {
            returns(listOf(expected))
        }

        infix fun returns(expected: List<Build>) {
            val results = filter.filterBranchBuilds(branch)
            assertEquals(
                    expected.map { it.id },
                    results.map { it.id }
            )
        }
    }

}