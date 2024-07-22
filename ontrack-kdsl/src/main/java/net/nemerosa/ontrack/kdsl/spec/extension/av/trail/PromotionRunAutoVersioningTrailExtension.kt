package net.nemerosa.ontrack.kdsl.spec.extension.av.trail

import net.nemerosa.ontrack.kdsl.connector.graphql.schema.PromotionRunAutoVersioningTrailQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.PromotionRun
import net.nemerosa.ontrack.kdsl.spec.extension.av.toAutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.toBranch

val PromotionRun.autoVersioningTrail: AutoVersioningTrail?
    get() =
        graphqlConnector.query(
            PromotionRunAutoVersioningTrailQuery(id.toInt())
        )?.promotionRuns()?.firstOrNull()
            ?.autoVersioningTrail()?.branches()
            ?.let { branches ->
                AutoVersioningTrail(
                    branches = branches.map { branch ->
                        AutoVersioningBranchTrail(
                            connector = connector,
                            branch = branch.branch().fragments().branchFragment().toBranch(this),
                            configuration = branch.configuration().fragments().autoVersioningSourceConfigFragment()
                                .toAutoVersioningSourceConfig(),
                            rejectionReason = branch.rejectionReason(),
                            orderId = branch.orderId(),
                        )
                    }
                )
            }
