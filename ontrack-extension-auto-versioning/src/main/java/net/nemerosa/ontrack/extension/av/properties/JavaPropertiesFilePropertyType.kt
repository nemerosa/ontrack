package net.nemerosa.ontrack.extension.av.properties

import org.springframework.stereotype.Component

@Component
class JavaPropertiesFilePropertyType : AbstractRegexFilePropertyType() {

    override val id: String = "properties"

    override fun getPropertyRegex(property: String?): String =
        "$property\\s*=\\s*(.*)"

}