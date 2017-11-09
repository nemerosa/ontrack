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

    @DSLMethod("Associates some data with the validation.")
    ValidationRun validateWithData(String validationStamp, Object data, String status = null) {
        new ValidationRun(
                ontrack,
                ontrack.post(
                        link("validate"),
                        [
                                validationStampData  : [
                                        id  : validationStamp,
                                        data: data,
                                ],
                                validationRunStatusId: status
                        ]
                )
        )
    }

    @DSLMethod("Associates some text with the validation. The validation stamp must be configured to accept text as validation data.")
    ValidationRun validateWithText(String validationStamp, String status, String text) {
        return validateWithData(
                validationStamp,
                [value: text],
                status
        )
    }

    @DSLMethod("""
        Associates some critical / high / medium / low issue counts with the validation. The 
        validation stamp must be configured to accept CHML as validation data.""")
    ValidationRun validateWithCHML(String validationStamp, int critical = 0, int high = 0, int medium = 0, int low = 0) {
        return validateWithData("VS", [
                CRITICAL: critical,
                HIGH    : high,
                MEDIUM  : medium,
                LOW     : low,
        ])
    }

    @DSLMethod("""
        Associates some number with the validation. The 
        validation stamp must be configured to accept number as validation data.""")
    ValidationRun validateWithNumber(String validationStamp, int value) {
        return validateWithData("VS", [value: value])
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
    String getReleaseDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.ReleaseDecorationExtension') as String
    }

    /**
     * Build links decorations.
     */
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

}
