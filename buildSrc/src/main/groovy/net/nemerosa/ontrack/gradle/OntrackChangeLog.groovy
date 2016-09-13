package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class OntrackChangeLog extends AbstractOntrackTask {

    String ontrackProject = 'ontrack'
    String ontrackReleasePromotionLevel = 'RELEASE'
    String ontrackReleaseBranch = project.properties.ontrackReleaseBranch
    String ontrackReleaseFilter = project.properties.ontrackReleaseFilter

    private String changeLog

    @TaskAction
    void run() {
        if (!ontrackReleaseBranch) throw new GradleException("Missing ontrackReleaseBranch property")
        println "[${name}] Getting the Ontrack log for ${ontrackProject} since " +
                "last ${ontrackReleasePromotionLevel} on branch ${ontrackReleaseBranch}"
        // Gets the Ontrack client
        def ontrack = ontrackClient
        // Gest the Ontrack project
        def project = ontrack.project(ontrackProject)
        // Gets the last build on the branch to release
        println "ontrackReleaseBranch = ${ontrackReleaseBranch}"
        def lastBuild = project.search(branchName: ontrackReleaseBranch)[0]
        // Gets the last release
        def lastRelease = project.search(
                branchName: ontrackReleaseFilter,
                promotionName: ontrackReleasePromotionLevel)[0]
        // Gets the change log
        def changeLog = lastBuild.getChangeLog(lastRelease)
        // Exports the issues
        this.changeLog = changeLog.exportIssues(
                format: 'text',
                groups: [
                        'Features'    : ['feature'],
                        'Enhancements': ['enhancement'],
                        'Bugs'        : ['bug'],
                ],
                exclude: ['design', 'delivery']

        )
    }

    String getChangeLog() {
        return changeLog
    }
}
