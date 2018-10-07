package net.nemerosa.ontrack.boot.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.model.structure.config
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * All tests with data associated to validation runs.
 *
 * Different axes of test:
 *
 * * validation stamp with or without data type
 * * validation stamp data type with or without computed status
 * * validation run with or without data
 * * validation run status provided or not
 * * validation run data valid or invalid
 */
class ValidationRunControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var validationRunController: ValidationRunController

    @Autowired
    private lateinit var validationRunStatusService: ValidationRunStatusService

    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

    @Test
    fun `Stamp with type, computed status, run with data, provided status, valid data`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun().withData(mapOf("value" to 40)).withStatus("PASSED")
                .execute()
                .mustBe("PASSED").withData(40)
    }

    @Test(expected = ValidationRunDataFormatException::class)
    fun `Stamp with type, computed status, run with data, provided status, invalid data`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun().withData("text".toJson()!!).withStatus("PASSED")
                .execute()
    }

    @Test
    fun `Stamp with type, computed status, run with data, unprovided status, valid data`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun().withData(mapOf("value" to 40))
                .execute()
                .mustBe("FAILED").withData(40)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun `Stamp with type, computed status, run with data, unprovided status, invalid data`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun().withData("text".toJson()!!)
                .execute()
    }

    @Test
    fun `Stamp with type, computed status, run without data, provided status`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun().withStatus("PASSED")
                .execute()
                .mustBe("PASSED").withNoData()
    }

    @Test(expected = ValidationRunDataStatusRequiredBecauseNoDataException::class)
    fun `Stamp with type, computed status, run without data, unprovided status`() {
        doTestVS().withType().withComputedStatus(50)
                .forRun()
                .execute()
    }

    @Test
    fun `Stamp with type, no computed status, run with data, provided status, valid data`() {
        doTestVS().withType()
                .forRun().withData(mapOf("value" to 40)).withStatus("PASSED")
                .execute()
                .mustBe("PASSED").withData(40)
    }

    @Test(expected = ValidationRunDataFormatException::class)
    fun `Stamp with type, no computed status, run with data, provided status, invalid data`() {
        doTestVS().withType()
                .forRun().withData("text".toJson()!!).withStatus("PASSED")
                .execute()
    }

    @Test
    fun `Stamp with type, no computed status, run with data, unprovided status, valid data`() {
        doTestVS().withType()
                .forRun().withData(mapOf("value" to 40))
                .execute()
                .mustBe("PASSED").withData(40)
    }

    @Test(expected = ValidationRunDataFormatException::class)
    fun `Stamp with type, no computed status, run with data, unprovided status, invalid data`() {
        doTestVS().withType()
                .forRun().withData("text".toJson()!!)
                .execute()
    }

    @Test
    fun `Stamp with type, no computed status, run without data, provided status`() {
        doTestVS().withType()
                .forRun().withStatus("FAILED")
                .execute()
                .mustBe("FAILED").withNoData()
    }

    @Test(expected = ValidationRunDataStatusRequiredBecauseNoDataException::class)
    fun `Stamp with type, no computed status, run without data, unprovided status`() {
        doTestVS().withType()
                .forRun()
                .execute()
    }

    @Test
    fun `Stamp without type, run with data, provided status, valid data`() {
        doTestVS()
                .forRun().withData(mapOf("value" to 40)).withStatus("FAILED")
                .execute()
                .mustBe("FAILED").withData(40)
    }

    @Test(expected = ValidationRunDataFormatException::class)
    fun `Stamp without type, run with data, provided status, invalid data`() {
        doTestVS()
                .forRun().withData("text".toJson()!!).withStatus("FAILED")
                .execute()
    }

    @Test(expected = ValidationRunDataStatusRequiredBecauseNoDataTypeException::class)
    fun `Stamp without type, run with data, unprovided status, valid data`() {
        doTestVS()
                .forRun().withData("value" to 40)
                .execute()
    }

    @Test(expected = ValidationRunDataFormatException::class)
    fun `Stamp without type, run with data, unprovided status, invalid data`() {
        doTestVS()
                .forRun().withData("text".toJson()!!)
                .execute()
    }

    @Test
    fun `Stamp without type, run without data, provided status`() {
        doTestVS()
                .forRun().withStatus("FAILED")
                .execute()
                .mustBe("FAILED").withNoData()
    }

    @Test(expected = ValidationRunDataStatusRequiredBecauseNoDataException::class)
    fun `Stamp without type, run without data, unprovided status`() {
        doTestVS()
                .forRun()
                .execute()
    }

    // ---

    @Test(expected = ValidationRunDataTypeNotFoundException::class)
    fun `Stamp without type, run with data, unprovided status, unknown type`() {
        doTestVS()
                .forRun().withData("value" to 50).withDataType("unknown")
                .execute()
    }

    private fun doTestVS() = VS()

    private inner class VS {

        var typed: Boolean = false
        var threshold: Int? = null

        fun withType() = apply {
            typed = true
        }

        fun withComputedStatus(value: Int) = apply {
            threshold = value
        }

        fun forRun() = VRun(this)

        fun <T> withValidationStamp(fn: (ValidationStamp) -> T): T =
                project<T> {
                    branch<T> {
                        val vs = validationStamp(
                                "VS",
                                if (typed) {
                                    testNumberValidationDataType.config(threshold)
                                } else {
                                    null
                                }
                        )
                        fn(vs)
                    }
                }
    }

    private inner class VRun(private val vs: VS) {
        private var data: JsonNode? = null
        private var dataType: String? = null
        private var status: String? = null

        fun withData(value: JsonNode) = apply {
            data = value
        }

        fun withData(map: Map<String, *>) = apply {
            data = map.toJson()!!
        }

        fun withData(pair: Pair<String, *>) = withData(mapOf(pair))

        fun withStatus(s: String) = apply {
            status = s
        }

        fun withDataType(s: String) = apply {
            dataType = s
        }

        fun execute(): VTest {
            val run = vs.withValidationStamp {
                it.branch.build<ValidationRun>("1.0.0") {
                    // Calling the validation run controller
                    validationRunController.newValidationRun(
                            id,
                            ValidationRunRequestForm(
                                    description = "",
                                    validationRunStatusId = status,
                                    validationStampData = ValidationRunRequestFormData(
                                            id = it.name,
                                            type = dataType ?: if (vs.typed) null else testNumberValidationDataType.descriptor.id,
                                            data = data
                                    )
                            )
                    )
                }
            }
            return VTest(run)
        }

    }

    private class VTest(private val run: ValidationRun) {
        fun mustBe(status: String) = apply {
            assertEquals(
                    status,
                    run.lastStatus.statusID.id
            )
        }

        fun withData(expectedValue: Int) = apply {
            assertNotNull(run.data) {
                assertIs<Int>(it.data) {
                    assertEquals(expectedValue, it)
                }
            }
        }

        fun withNoData() = apply {
            assertNull(run.data)
        }
    }

}