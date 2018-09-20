package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.BuildProperties

@DSL
class Build extends AbstractProjectResource {

    Build(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Configuration of the build in a closure.")
    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @DSLMethod("Gets the build project name.")
    String getProject() {
        node?.branch?.project?.name
    }

    @DSLMethod("Gets the build branch name.")
    String getBranch() {
        node?.branch?.name
    }

    @DSLMethod("Promotes this build to the given promotion level.")
    PromotionRun promote(String promotion) {
        new PromotionRun(
                ontrack,
                ontrack.post(link('promote'), [
                        promotionLevelName: promotion,
                        description       : '',
                ])
        )
    }

    @DSLMethod("Promotes this build to the given promotion level and configures the created <<dsl-promotionrun,promotion run>>.")
    PromotionRun promote(String promotion, Closure closure) {
        def run = promote(promotion)
        run(closure)
        run
    }

    @DSLMethod(id = "validate", count = 2)
    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED') {
        new ValidationRun(
                ontrack,
                ontrack.post(link('validate'), [
                        validationStampName  : validationStamp,
                        validationRunStatusId: validationStampStatus,
                        description          : ''
                ])
        )
    }

    @DSLMethod(id = "validate-closure", count = 3)
    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED', Closure closure) {
        def run = validate(validationStamp, validationStampStatus)
        run(closure)
        run
    }

    @DSLMethod("Gets the list of promotion runs for this build")
    List<PromotionRun> getPromotionRuns() {
        ontrack.get(link('promotionRuns')).resources.collect {
            new PromotionRun(ontrack, it)
        }
    }

    @DSLMethod("Gets the list of validation runs for this build")
    List<ValidationRun> getValidationRuns() {
        ontrack.get(link('validationRuns')).resources.collect {
            new ValidationRun(ontrack, it)
        }
    }

    @DSLMethod
    BuildProperties getConfig() {
        new BuildProperties(ontrack, this)
    }

    /**
     * Sets the signature of the build. This method is granted only for users having the
     * <code>ProjectEdit</code> function: administrators, project owners, project managers.
     *
     * Date is expected to be UTC.
     */
    @DSLMethod(id = "signature", count = 2)
    def signature(String user = null, Date date = null) {
        ontrack.put(
                link('signature'),
                [
                        user: user,
                        time: date ? date.format("yyyy-MM-dd'T'HH:mm:ss") : null
                ]
        )
    }

    /**
     * Previous build
     * @return Null if none
     */
    @DSLMethod("Returns the previous build in the same branch, or `null` if there is none.")
    Build getPreviousBuild() {
        def json = ontrack.get(link('previous'))
        if (json) {
            return new Build(ontrack, json)
        } else {
            return null
        }
    }

    /**
     * Next build
     * @return Null if none
     */
    @DSLMethod("Returns the next build in the same branch, or `null` if there is none.")
    Build getNextBuild() {
        def json = ontrack.get(link('next'))
        if (json) {
            return new Build(ontrack, json)
        } else {
            return null
        }
    }

    /**
     * Gets the change log between this build and another one.
     *
     * If no change log is available, because the associated branch is not configured for example,
     * null is returned.
     */
    @DSLMethod("Computes the <<changelogs,change log>> between this build and the one given in parameter.")
    ChangeLog getChangeLog(Build otherBuild) {
        try {
            return new ChangeLog(
                    ontrack,
                    ontrack.get(
                            query(
                                    link('changeLog'),
                                    [
                                            from: otherBuild.id,
                                            to  : this.id,
                                    ]
                            )
                    )
            )
        } catch (ResourceMissingLinkException ignored) {
            return null
        }
    }

    /**
     * Release decoration
     */
    @DSLMethod("Returns any label associated with this build.")
    String getReleaseDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.ReleaseDecorationExtension') as String
    }

    /**
     * Build links decorations.
     */
    @DSLMethod("Returns the build links associated with this build")
    List<?> getBuildLinkDecorations() {
        getDecorations('net.nemerosa.ontrack.extension.general.BuildLinkDecorationExtension')
    }

    /**
     * Build links
     */

    @DSLMethod
    def buildLink(String project, String build) {
        ontrack.put(
                link('buildLinks'),
                [
                        addOnly: true,
                        links  : [[
                                          project: project,
                                          build  : build,
                                  ]]
                ]
        )
    }

    @DSLMethod(see = "buildLink")
    List<Build> getBuildLinks() {
        return ontrack.get(link('buildLinksFrom')).resources.collect {
            new Build(ontrack, it)
        }
    }

    /**
     * SVN revision decoration
     */
    @DSLMethod
    Long getSvnRevisionDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.svn.SVNRevisionDecorationExtension') as Long
    }

    @DSLMethod("Gets the associated run info with this build, or `null` if none")
    RunInfo getRunInfo() {
        def result = ontrack.get(link("runInfo"))
        def info = new RunInfo(ontrack, result)
        return info.id != 0 ? info : null
    }

    @DSLMethod("Sets the run info for this build.")
    void setRunInfo(Map<String, ?> info) {
        ontrack.put(
                link("runInfo"),
                info
        )
    }
}
