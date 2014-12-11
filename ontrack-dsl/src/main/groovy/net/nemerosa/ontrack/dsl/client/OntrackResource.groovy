package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.dsl.*

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

    @Override
    ValidationStamp validationStamp(String project, String branch, String validationStamp) {
        new ValidationStampResource(
                this,
                get("structure/entity/validationStamp/${project}/${branch}/${validationStamp}")
        )
    }

    @Override
    Build build(String project, String branch, String build) {
        new BuildResource(
                this,
                get("structure/entity/build/${project}/${branch}/${build}")
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
