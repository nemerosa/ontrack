package net.nemerosa.ontrack.gradle

import net.nemerosa.ontrack.dsl.Build
import org.gradle.api.tasks.TaskAction

@SuppressWarnings("GroovyUnusedDeclaration")
class OntrackLastReleases extends AbstractOntrackTask {

    String ontrackProject = 'ontrack'
    String ontrackReleasePromotionLevel = 'RELEASE'
    int releaseCount = 5
    String releasePattern = '.*'

    private List<Build> releases = []

    @TaskAction
    void run() {
        println "[${name}] Getting the last releases"
        // Gets the Ontrack client
        def ontrack = getOntrackClient(false)
        // Gets the Ontrack project
        def project = ontrack.project(ontrackProject)
        // List of releases
        List<Build> result = []
        // Gets all branches
        int count = 0
        project.branches.each { branch ->
            // Only release/ branches
            if (count < releaseCount && branch.name ==~ /.*release-.*/) {
                // ... and gets the last RELEASE build for each of them
                List<Build> builds = branch.standardFilter count: 1, withPromotionLevel: ontrackReleasePromotionLevel
                if (!builds.empty) {
                    def build = builds[0]
                    if (build.name ==~ releasePattern) {
                        result.add build
                        count++
                    }
                }
            }
        }
        this.releases = result
    }

    List<Build> getReleases() {
        return releases
    }
}
