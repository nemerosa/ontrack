package net.nemerosa.ontrack.extension.sonarqube.client.model

abstract class PagedResult(
        val paging: Paging
) {
    abstract val isEmpty: Boolean
}