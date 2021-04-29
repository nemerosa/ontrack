package net.nemerosa.ontrack.graphql.schema.links

import net.nemerosa.ontrack.model.links.BranchLinksService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBranchGraphFieldContributor(
    gqlEnumBranchLinksDirection: GQLEnumBranchLinksDirection,
    gqlTypeBranchLinksNode: GQLTypeBranchLinksNode,
    branchLinksService: BranchLinksService
) : AbstractGQLGraphFieldContributor<Branch>(
    projectEntityType = ProjectEntityType.BRANCH,
    description = "Graph of dependencies for this branch",
    fetcher = { branch, direction -> branchLinksService.getBranchLinks(branch, direction) },
    gqlEnumBranchLinksDirection = gqlEnumBranchLinksDirection,
    gqlTypeBranchLinksNode = gqlTypeBranchLinksNode,
    branchLinksService = branchLinksService
)