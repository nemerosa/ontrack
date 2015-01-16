package net.nemerosa.ontrack.dsl.client

import net.nemerosa.ontrack.dsl.Document
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PromotionLevel
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.PromotionLevelProperties

class PromotionLevelResource extends AbstractProjectResource implements PromotionLevel {

    PromotionLevelResource(Ontrack ontrack, Object node) {
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
        new PromotionLevelProperties(ontrack, this)
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
