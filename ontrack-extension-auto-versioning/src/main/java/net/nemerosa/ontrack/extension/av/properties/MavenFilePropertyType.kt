package net.nemerosa.ontrack.extension.av.properties

import org.springframework.stereotype.Component
import org.w3c.dom.Document

@Component
class MavenFilePropertyType : AbstractXPathFilePropertyType() {

    override val id: String = "maven"

    override fun readProperty(doc: Document, targetProperty: String?): String? {
        return if (targetProperty.isNullOrBlank()) {
            null
        } else {
            getValueByXPath(doc, "/project/properties/*[name()='$targetProperty']")
        }
    }

    override fun replaceProperty(doc: Document, targetProperty: String?, targetVersion: String) {
        if (!targetProperty.isNullOrBlank()) {
            setValueByXPath(doc, "/project/properties/*[name()='$targetProperty']", targetVersion)
        }
    }

}