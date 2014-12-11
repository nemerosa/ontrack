package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.json.JsonUtils

class BuildResource extends AbstractProjectResource implements Build {

    BuildResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('branch').path('project'), 'name')
    }

    @Override
    String getBranch() {
        JsonUtils.get(node.path('branch'), 'name')
    }

    @Override
    Build promote(String promotion) {
        post(link('promote'), [
                promotionLevel: ontrack.promotionLevel(project, branch, promotion).id,
        ])
        this
    }
}
