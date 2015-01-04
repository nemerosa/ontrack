package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PromotionLevel
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.PromotionLevelProperties
import net.nemerosa.ontrack.json.JsonUtils

class PromotionLevelResource extends AbstractProjectResource implements PromotionLevel {

    PromotionLevelResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('project'), 'name')
    }

    @Override
    String getBranch() {
        JsonUtils.get(node.path('project').path('branch'), 'name')
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
        // FIXME Method net.nemerosa.ontrack.dsl.PromotionLevel.image
        return null
    }

    @Override
    Document getImage() {
        // FIXME Method net.nemerosa.ontrack.dsl.PromotionLevel.getImage
        return null
    }
}
