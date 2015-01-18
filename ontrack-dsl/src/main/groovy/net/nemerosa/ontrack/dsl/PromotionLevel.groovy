package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.PromotionLevelProperties

class PromotionLevel extends AbstractProjectResource {

    PromotionLevel(Ontrack ontrack, Object node) {
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

    ProjectEntityProperties getProperties() {
        new PromotionLevelProperties(ontrack, this)
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
}
