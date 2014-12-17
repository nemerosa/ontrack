package net.nemerosa.ontrack.dsl

interface Ontrack extends OntrackConnector {

    /**
     * Gets a project by its name, or creates it if it does not exist
     */
    Project project(String name)

    /**
     * Gets a project by its name, or creates it if it does not exist
     */
    Project project(String name, Closure closure)

    Branch branch(String project, String branch)

    PromotionLevel promotionLevel(String project, String branch, String promotionLevel)

    ValidationStamp validationStamp(String project, String branch, String validationStamp)

    Build build(String project, String branch, String build)

    /**
     * General configuration of Ontrack
     */
    def configure(Closure closure)
}
