package net.nemerosa.ontrack.graphql.schema.links

import net.nemerosa.ontrack.model.links.BranchLinksService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBuildGraphFieldContributor(
    gqlEnumBranchLinksDirection: GQLEnumBranchLinksDirection,
    gqlTypeBranchLinksNode: GQLTypeBranchLinksNode,
    branchLinksService: BranchLinksService
) : AbstractGQLGraphFieldContributor<Build>(
    projectEntityType = ProjectEntityType.BUILD,
    description = "Graph of dependencies for this build",
    fetcher = { build, direction -> branchLinksService.getBuildLinks(build, direction) },
    gqlEnumBranchLinksDirection = gqlEnumBranchLinksDirection,
    gqlTypeBranchLinksNode = gqlTypeBranchLinksNode,
    branchLinksService = branchLinksService
)
