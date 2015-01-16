package net.nemerosa.ontrack.dsl.client

import net.nemerosa.ontrack.dsl.Document
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ValidationStamp
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.ValidationStampProperties

class ValidationStampResource extends AbstractProjectResource implements ValidationStamp {

    ValidationStampResource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        node?.branch?.project?.name
    }

    @Override
    String getBranch() {
        node?.branch?.name
    }

    @Override
    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @Override
    ProjectEntityProperties getProperties() {
        new ValidationStampProperties(ontrack, this)
    }

    @Override
    def image(Object o) {
        image(o, 'image/png')
    }

    @Override
    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    @Override
    Document getImage() {
        ontrack.download(link('image'))
    }
}
