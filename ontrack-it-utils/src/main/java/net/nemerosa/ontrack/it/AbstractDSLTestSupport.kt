package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

abstract class AbstractDSLTestSupport : AbstractServiceTestSupport() {

    @Autowired
    protected lateinit var securityService: SecurityService

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

    fun Branch.promotionLevel(name: String): PromotionLevel =
            doCreatePromotionLevel(this, NameDescription.nd(name, ""))

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

    fun Branch.build(name: String, init: (Build.() -> Unit)? = {}): Build {
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
            validationRunStatusID: ValidationRunStatusID = ValidationRunStatusID.STATUS_PASSED
    ): ValidationRun {
        return this.validateWithData<Any>(
                validationStamp,
                validationRunStatusID
        )
    }

    /**
     * Creates a validation run on a build, possibly with some data and a status.
     */
    fun <T> Build.validateWithData(
            validationStamp: ValidationStamp,
            validationRunStatusID: ValidationRunStatusID? = null,
            validationDataTypeId: String? = null,
            validationRunData: T? = null
    ) = validateWithData(
            validationStamp.name,
            validationRunStatusID,
            validationDataTypeId,
            validationRunData
    )

    /**
     * Creates a validation run on a build, possibly with some data and a status.
     */
    fun <T> Build.validateWithData(
            validationStampName: String,
            validationRunStatusID: ValidationRunStatusID? = null,
            validationDataTypeId: String? = null,
            validationRunData: T? = null
    ): ValidationRun {
        return asUser().with(this, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    this,
                    ValidationRunRequest(
                            validationRunStatusId = validationRunStatusID?.id,
                            validationStampData = ValidationRunDataRequest(
                                    name = validationStampName,
                                    type = validationDataTypeId,
                                    data = validationRunData
                            )
                    )
            )
        }
    }

    fun Build.linkTo(project: Project, buildName: String) {
        val build = structureService.buildSearch(
                project.id,
                BuildSearchForm().withBuildExactMatch(true).withBuildName(buildName)
        ).first() ?: throw BuildNotFoundException(project.name, buildName)
        structureService.addBuildLink(
                this,
                build
        )
    }

}