package net.nemerosa.ontrack.extension.sonarqube.client.model

class ProjectAnalysisSearch(
        paging: Paging,
        val analyses: List<Analysis>
) : PagedResult(paging) {
    override val isEmpty: Boolean = analyses.isEmpty()
}
