package net.nemerosa.ontrack.extension.casc

import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import net.nemerosa.ontrack.extension.casc.schema.json.CascJsonSchemaService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.yaml.Yaml
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Support for testing CasC
 */
abstract class AbstractCascTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    @Autowired
    protected lateinit var cascJsonSchemaService: CascJsonSchemaService

    private val jsonSchemaFactory: JsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)

    /**
     * Runs a CasC from a series of YAML texts
     */
    protected fun casc(vararg yaml: String) {
        asAdmin {
            cascService.runYaml(*yaml)
        }
    }

    protected fun assertValidYaml(yamlSource: String) {
        val validationMessages = validateYaml(yamlSource)
        if (validationMessages.isNotEmpty()) {
            validationMessages.forEach {
                println("* ${it.message}")
            }
            fail("YAML failed to validate")
        }
    }

    protected fun assertInvalidYaml(yamlSource: String, message: String) {
        val validationMessages = validateYaml(yamlSource)
        assertTrue(
            validationMessages.any {
                it.message.equals(message, ignoreCase = true)
            },
            "Expected validation message to be equal to $message but was $validationMessages",
        )
    }

    protected fun validateYaml(yamlSource: String): Set<ValidationMessage> {
        val schemaNode = asAdmin {
            cascJsonSchemaService.createCascJsonSchema()
        }
        val yamlNode = Yaml().read(yamlSource).single()
        val schema: JsonSchema = jsonSchemaFactory.getSchema(schemaNode)
        return schema.validate(yamlNode)
    }


}