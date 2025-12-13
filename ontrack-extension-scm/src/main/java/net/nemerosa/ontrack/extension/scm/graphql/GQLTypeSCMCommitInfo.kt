package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import jakarta.validation.constraints.NotNull
import net.nemerosa.ontrack.extension.scm.SCMCommitInfo
import net.nemerosa.ontrack.extension.scm.model.BranchInfos
import net.nemerosa.ontrack.extension.scm.service.BranchInfoService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listFieldGetter
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCommitInfo(
    private val branchInfoService: BranchInfoService,
) : GQLType {

    override fun getTypeName(): String = SCMCommitInfo::class.java.simpleName

    override fun createType(cache: @NotNull GQLTypeCache): @NotNull GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about a commit")
            .field(SCMCommitInfo::scmDecoratedCommit)
            .listFieldGetter<SCMCommitInfo, BranchInfos>(
                name = "branchInfos",
                description = "Information about the branches associated with this commit",
            ) { scmCommitInfo ->
                val (project, commit) = scmCommitInfo.scmDecoratedCommit
                branchInfoService.getBranchInfos(project, commit.id)
            }
            // OK
            .build()
}