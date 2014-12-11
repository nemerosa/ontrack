package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnector

/**
 * Entry point for the DSL.
 */
class OntrackClient implements Ontrack, OntrackConnector {

    /**
     * JSON client
     */
    private final JsonClient jsonClient

    /**
     * Construction of the Ontrack client, based on a raw JSON client
     */
    OntrackClient(JsonClient jsonClient) {
        this.jsonClient = jsonClient
    }

    /**
     * Gets a branch in a project by its name
     */
    @Override
    Branch branch(String project, String branch) {
        new BranchClient(
                this,
                get("structure/entity/branch/${project}/${branch}")
        )
    }

    JsonNode get(String url) {
        jsonClient.get(url)
    }
}
