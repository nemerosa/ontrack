package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty


const val JENKINS_BUILD_PROPERTY = "net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType"

var Build.jenkinsBuild: JenkinsBuildProperty?
    get() = getProperty(JENKINS_BUILD_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(JENKINS_BUILD_PROPERTY, value)
        } else {
            deleteProperty(JENKINS_BUILD_PROPERTY)
        }
    }

@JsonDeserialize(using = JenkinsBuildPropertyDeserializer::class)
data class JenkinsBuildProperty(
    val configuration: String,
    val job: String,
    val build: Int,
    /**
     * This property is filled in only on read.
     */
    val url: String? = null,
)

class JenkinsBuildPropertyDeserializer : JsonDeserializer<JenkinsBuildProperty>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JenkinsBuildProperty {
        val node: JsonNode = p.readValueAsTree()
        return JenkinsBuildProperty(
            configuration = node.path("configuration").path("name").asText(),
            job = node.path("job").asText(),
            build = node.path("build").asInt(),
            url = node.path("url").asText(),
        )
    }

}