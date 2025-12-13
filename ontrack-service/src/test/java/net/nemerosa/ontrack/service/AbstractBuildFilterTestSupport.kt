package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractBuildFilterTestSupport : AbstractDSLTestSupport() {

    protected lateinit var branch: Branch
    protected lateinit var copper: PromotionLevel
    protected lateinit var bronze: PromotionLevel
    protected lateinit var gold: PromotionLevel
    protected lateinit var publication: ValidationStamp
    protected lateinit var production: ValidationStamp

    @BeforeEach
    fun prepare() {
        project {
            branch {
                this@AbstractBuildFilterTestSupport.branch = this
                copper = promotionLevel("COPPER")
                bronze = promotionLevel("BRONZE")
                gold = promotionLevel("GOLD")
                publication = validationStamp("PUBLICATION")
                production = validationStamp("PRODUCTION")
            }
        }
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
            val build = asUser().withProjectFunction(branch, BuildCreate::class.java).call {
                structureService.newBuild(
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
            asUser().withProjectFunction(branch, PromotionRunCreate::class.java).call {
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
            asUser().withView(branch).withProjectFunction(branch, ValidationRunCreate::class.java).call {
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
                    .withProjectFunction(branch, ProjectView::class.java)
                    .withProjectFunction(otherBuild, BuildEdit::class.java)
                    .execute {
                        structureService.createBuildLink(
                            fromBuild = otherBuild,
                            toBuild = build,
                            qualifier = BuildLink.DEFAULT,
                        )
                    }
            return this
        }

        @Throws(Exception::class)
        fun linkedTo(otherBuild: Build): BuildCreator {
            asUser()
                    .withProjectFunction(branch, BuildEdit::class.java)
                    .withProjectFunction(otherBuild, ProjectView::class.java)
                    .execute {
                        structureService.createBuildLink(
                            fromBuild = build,
                            toBuild = otherBuild
                        )
                    }
            return this
        }

        @Throws(Exception::class)
        fun withProperty(value: String): BuildCreator {
            asUser().withProjectFunction(build, ProjectEdit::class.java).call {
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

    protected fun checkListIsEmpty(builds: List<Build>) {
        assertTrue(builds.isEmpty(), "Build list is empty")
    }

}