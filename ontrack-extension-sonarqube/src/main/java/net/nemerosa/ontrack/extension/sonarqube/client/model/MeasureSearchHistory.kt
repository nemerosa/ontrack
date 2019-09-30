package net.nemerosa.ontrack.extension.sonarqube.client.model

class MeasureSearchHistory(
        paging: Paging,
        val measures: List<Measure>
) : PagedResult(paging) {
    override val isEmpty: Boolean = measures.isEmpty()
}