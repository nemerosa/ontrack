package net.nemerosa.ontrack.dsl

interface Ontrack extends OntrackConnector {

    Branch branch(String project, String branch)

    PromotionLevel promotionLevel(String project, String branch, String promotionLevel)
}
