package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestResult

fun JestResult.checkResult() {
    if (!isSucceeded) {
        throw ElasticSearchException("[${responseCode}] $errorMessage $jsonString")
    }
}
