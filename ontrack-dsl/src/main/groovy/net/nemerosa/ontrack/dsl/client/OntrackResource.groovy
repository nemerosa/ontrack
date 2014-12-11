package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnector
import net.nemerosa.ontrack.dsl.PromotionLevel

/**
 * Entry point for the DSL.
 */
class OntrackResource implements Ontrack, OntrackConnector {

    /**
     * JSON client
     */
    private final JsonClient jsonClient

    /**
     * Construction of the Ontrack client, based on a raw JSON client
     */
    OntrackResource(JsonClient jsonClient) {
        this.jsonClient = jsonClient
    }

    @Override
    Branch branch(String project, String branch) {
        new BranchResource(
                this,
                get("structure/entity/branch/${project}/${branch}")
        )
    }

    @Override
    PromotionLevel promotionLevel(String project, String branch, String promotionLevel) {
        new PromotionLevelResource(
                this,
                get("structure/entity/promotionLevel/${project}/${branch}/${promotionLevel}")
        )
    }

    JsonNode get(String url) {
        jsonClient.get(url)
    }

    @Override
    JsonNode post(String url, Object data) {
        jsonClient.post(
                jsonClient.toNode(data),
                url
        )
    }
}
