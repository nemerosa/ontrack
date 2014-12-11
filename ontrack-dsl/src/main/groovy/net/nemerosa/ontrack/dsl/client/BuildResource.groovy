package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PromotionLevel
import net.nemerosa.ontrack.json.JsonUtils

class BuildResource extends AbstractProjectResource implements Build {

    BuildResource(Ontrack ontrack, JsonNode node) {
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
    Build promote(PromotionLevel promotionLevel) {
        post(link('promote'), [
                promotionLevel: promotionLevel.id,
        ])
        this
    }
}
