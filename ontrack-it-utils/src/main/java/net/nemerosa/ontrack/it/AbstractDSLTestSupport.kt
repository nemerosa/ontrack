package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
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

    fun Project.branch(name: String = uid("B"), init: Branch.() -> Unit = {}): Branch {
        val branch = doCreateBranch(this, NameDescription.nd(name, ""))
        branch.init()
        return branch
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
    fun Branch.validationStamp(name: String = uid("VS")): ValidationStamp =
            doCreateValidationStamp(this, NameDescription.nd(name, ""))

    fun Branch.build(name: String, init: (Build.() -> Unit)? = {}): Build {
        val build = doCreateBuild(this, NameDescription.nd(name, ""))
        if (init != null) {
            build.init()
        }
        return build
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
    fun Build.validate(validationStamp: ValidationStamp, validationRunStatusID: ValidationRunStatusID = ValidationRunStatusID.STATUS_PASSED) {
        doValidateBuild(this, validationStamp, validationRunStatusID)
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