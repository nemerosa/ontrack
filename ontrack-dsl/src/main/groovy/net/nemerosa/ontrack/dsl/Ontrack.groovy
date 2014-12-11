package net.nemerosa.ontrack.dsl

interface Ontrack extends OntrackConnector {

    Branch branch(String project, String branch)

    PromotionLevel promotionLevel(String project, String branch, String promotionLevel)

    ValidationStamp validationStamp(String project, String branch, String validationStamp)

    Build build(String project, String branch, String build)
}
