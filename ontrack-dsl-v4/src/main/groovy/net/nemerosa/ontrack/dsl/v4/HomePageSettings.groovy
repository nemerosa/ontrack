package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod


@DSL("Home page settings")
class HomePageSettings {
    private final int maxBranches
    private final int maxProjects

    HomePageSettings(int maxBranches, int maxProjects) {
        this.maxBranches = maxBranches
        this.maxProjects = maxProjects
    }

    @DSLMethod("Maximum of branches to display per favorite project")
    int getMaxBranches() {
        return maxBranches
    }

    @DSLMethod("Maximum of projects starting from which we need to switch to a search mode")
    int getMaxProjects() {
        return maxProjects
    }
}
