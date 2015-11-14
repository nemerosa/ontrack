package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.ValidationStampProperties

class ValidationStamp extends AbstractProjectResource {

    ValidationStamp(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getProject() {
        node?.branch?.project?.name
    }

    String getBranch() {
        node?.branch?.name
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    ProjectEntityProperties getConfig() {
        new ValidationStampProperties(ontrack, this)
    }

    def image(Object o) {
        image(o, 'image/png')
    }

    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    Document getImage() {
        ontrack.download(link('image'))
    }

    /**
     * Validation stamp weather decoration: <code>weather</code> and <code>text</code>
     */
    def getValidationStampWeatherDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.ValidationStampWeatherDecorationExtension')
    }
}
