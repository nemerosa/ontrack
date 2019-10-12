package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import org.junit.Assert.assertEquals
import org.junit.Before
import java.time.LocalDateTime

@Deprecated("Use directly AbstractDSLTestSupport instead")
abstract class AbstractBuildFilterIT : AbstractDSLTestSupport() {

    protected lateinit var branch: Branch
    protected lateinit var copper: PromotionLevel
    protected lateinit var bronze: PromotionLevel
    protected lateinit var gold: PromotionLevel
    protected lateinit var publication: ValidationStamp
    protected lateinit var production: ValidationStamp

    @Before
    fun prepare() {
        branch = doCreateBranch()
        copper = doCreatePromotionLevel(branch, nd("COPPER", ""))
        bronze = doCreatePromotionLevel(branch, nd("BRONZE", ""))
        gold = doCreatePromotionLevel(branch, nd("GOLD", ""))
        publication = doCreateValidationStamp(branch, nd("PUBLICATION", ""))
        production = doCreateValidationStamp(branch, nd("PRODUCTION", ""))
    }

    protected fun build(name: Int): BuildCreator {
        return build(name.toString())
    }

    protected fun build(name: Int, dateTime: LocalDateTime): BuildCreator {
        return build(name.toString(), dateTime)
    }

    @JvmOverloads
    protected fun build(name: String, dateTime: LocalDateTime = LocalDateTime.of(2014, 7, 14, 13, 25, 0)): BuildCreator {
        try {
            val build = asUser().with(branch, BuildCreate::class.java).call {
                structureService!!.newBuild(
                        Build.of(
                                branch,
                                NameDescription(name, "Build $name"),
                                Signature.of("user").withTime(dateTime)
                        )
                )
            }
            return BuildCreator(build)
        } catch (ex: Exception) {
            throw RuntimeException("Cannot create build $name", ex)
        }

    }

    protected inner class BuildCreator(
            val build: Build
    ) {

        fun withPromotion(promotionLevel: PromotionLevel): BuildCreator {
            asUser().with(branch, PromotionRunCreate::class.java).call {
                structureService.newPromotionRun(
                        PromotionRun.of(
                                build,
                                promotionLevel,
                                Signature.of("user"),
                                ""
                        )
                )
            }
            return this
        }

        @Throws(Exception::class)
        fun withValidation(stamp: ValidationStamp, status: ValidationRunStatusID): BuildCreator {
            asUser().withView(branch).with(branch, ValidationRunCreate::class.java).call {
                structureService.newValidationRun(
                        build,
                        ValidationRunRequest(
                                stamp.name,
                                status
                        )
                )
            }
            return this
        }

        @Throws(Exception::class)
        fun linkedFrom(otherBuild: Build): BuildCreator {
            asUser()
                    .with(branch, ProjectView::class.java)
                    .with(otherBuild, BuildEdit::class.java)
                    .call<Any> {
                        structureService!!.addBuildLink(
                                otherBuild,
                                build
                        )
                        null
                    }
            return this
        }

        @Throws(Exception::class)
        fun linkedTo(otherBuild: Build): BuildCreator {
            asUser()
                    .with(branch, BuildEdit::class.java)
                    .with(otherBuild, ProjectView::class.java)
                    .call<Any> {
                        structureService!!.addBuildLink(
                                build,
                                otherBuild
                        )
                        null
                    }
            return this
        }

        @Throws(Exception::class)
        fun withProperty(value: String): BuildCreator {
            asUser().with(build, ProjectEdit::class.java).call<Ack> {
                propertyService.editProperty(
                        build,
                        TestSimplePropertyType::class.java,
                        TestSimpleProperty(value)
                )
            }
            return this
        }
    }

    protected fun checkList(builds: List<Build>, vararg ids: Int) {
        val expectedNames = ids
                .map { it.toString() }
        val actualNames = builds
                .map { it.name }
        assertEquals(expectedNames, actualNames)
    }

    protected fun checkList(builds: List<Build>, vararg expectedNames: String) {
        val actualNames = builds
                .map { it.name }
        assertEquals(expectedNames.toList(), actualNames)
    }

}