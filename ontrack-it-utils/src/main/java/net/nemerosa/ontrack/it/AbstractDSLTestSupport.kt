package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.buildfilter.StandardFilterProviderDataBuilder
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.labels.*
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
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
    protected final inline fun <reified T : GlobalFunction> asUserWith(noinline code: () -> Unit): Unit = asUserWith<T, Unit>(code)

    protected final inline fun <reified T : GlobalFunction, R> asUserWith(noinline code: () -> R): R =
            asUser().with(T::class.java).call(code)

    /**
     * Kotlin friendly
     */
    fun asUserWithView(vararg entities: ProjectEntity, code: () -> Unit) {
        asUserWithView(*entities).execute(code)
    }

    fun <T> ProjectEntity.asUserWithView(code: () -> T): T = asUserWithView(this).call(code)

    /**
     * Kotlin friendly anonymous execution
     */
    fun <T> asAnonymous(code: () -> T): T = asAnonymous().call(code)

    /**
     * Kotlin friendly admin execution
     */
    fun <T> asAdmin(code: () -> T): T = asAdmin().call(code)

    /**
     * Kotlin friendly account role execution
     */
    fun <T> ProjectEntity.asAccountWithProjectRole(role: String, code: () -> T): T {
        val account = doCreateAccountWithProjectRole(project, role)
        return asAccount(account).call(code)
    }

    /**
     * Kotlin friendly account role execution
     */
    fun <T> asAccountWithGlobalRole(role: String, code: () -> T): T {
        val account = doCreateAccountWithGlobalRole(role)
        return asAccount(account).call(code)
    }

    fun <T> withDisabledConfigurationTest(code: () -> T): T {
        val configurationTest = ontrackConfigProperties.configurationTest
        ontrackConfigProperties.configurationTest = false
        return try {
            code()
        } finally {
            ontrackConfigProperties.configurationTest = configurationTest
        }
    }

    fun project(name: NameDescription = AbstractITTestSupport.nameDescription(), init: Project.() -> Unit = {}): Project {
        val project = doCreateProject(name)
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

    /**
     * Deletes a branch
     */
    fun Branch.delete() {
        asAdmin {
            structureService.deleteBranch(id)
        }
    }

    fun Branch.promotionLevel(name: String = uid("P"), description: String = "", init: PromotionLevel.() -> Unit = {}): PromotionLevel =
            doCreatePromotionLevel(this, NameDescription.nd(name, description)).apply {
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

    /**
     * Deletes a validation stamp
     */
    fun ValidationStamp.delete() {
        asAdmin {
            structureService.deleteValidationStamp(id)
        }
    }

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

    protected fun <T, P : PropertyType<T>> ProjectEntity.property(type: KClass<P>, value: T?) {
        if (value != null) {
            propertyService.editProperty<T>(
                    this,
                    type.java,
                    value
            )
        } else {
            propertyService.deleteProperty(
                    this,
                    type.java
            )
        }
    }

    protected fun <T, P : PropertyType<T>> ProjectEntity.property(type: KClass<P>): T? =
            propertyService.getProperty(this, type.java).value

    fun Build.promote(promotionLevel: PromotionLevel, description: String = "", signature: Signature = Signature.of("test")) {
        doPromote(this, promotionLevel, description, signature)
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
            description: String? = null,
            code: ValidationRun.() -> Unit = {}
    ): ValidationRun {
        return this.validateWithData<Any>(
                validationStampName = validationStamp.name,
                validationRunStatusID = validationRunStatusID,
                description = description
        ).apply {
            code()
        }
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

    /**
     * Deletes a build
     */
    fun Build.delete() {
        asAdmin {
            structureService.deleteBuild(id)
        }
    }

    fun Build.linkTo(project: Project, buildName: String) {
        val build = structureService.buildSearch(
                project.id,
                BuildSearchForm().withBuildExactMatch(true).withBuildName(buildName)
        ).firstOrNull() ?: throw BuildNotFoundException(project.name, buildName)
        linkTo(build)
    }

    infix fun Build.linkTo(build: Build) {
        structureService.addBuildLink(
                this,
                build
        )
    }

    fun Build.unlinkTo(build: Build) {
        structureService.deleteBuildLink(
                this,
                build
        )
    }

    /**
     * Change of status for a validation run
     */
    fun ValidationRun.validationStatus(status: ValidationRunStatusID, description: String): ValidationRun {
        return asUser().with(this, ValidationRunStatusChange::class.java).call {
            structureService.newValidationRunStatus(
                    this,
                    ValidationRunStatus(
                            ID.NONE,
                            Signature.of("test"),
                            status,
                            description
                    )
            )
        }
    }

    /**
     * Change of status for a validation run
     */
    fun ValidationRun.validationStatusWithCurrentUser(status: ValidationRunStatusID, description: String): ValidationRun {
        return structureService.newValidationRunStatus(
                this,
                ValidationRunStatus(
                        ID.NONE,
                        securityService.currentSignature,
                        status,
                        description
                )
        )
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
    protected final inline fun <reified T: Any> withSettings(noinline code: () -> Unit) {
        withSettings(T::class, code)
    }

    /**
     * Saving current settings, runs some code and restores the format settings
     */
    protected fun <T: Any> withSettings(settingsClass: KClass<T>, code: () -> Unit) {
        val settings: T = settingsService.getCachedSettings(settingsClass.java)
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