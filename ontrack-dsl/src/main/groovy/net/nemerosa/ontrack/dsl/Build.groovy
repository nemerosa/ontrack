package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.BuildProperties

import java.text.SimpleDateFormat

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

    @DSLMethod(id="promote-closure", value = "Promotes this build to the given promotion level and configures the created <<dsl-promotionrun,promotion run>>.")
    PromotionRun promote(String promotion, Closure closure) {
        def run = promote(promotion)
        run(closure)
        run
    }

    @DSLMethod(id = "validate", count = 3)
    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED', String description = "") {
        new ValidationRun(
                ontrack,
                ontrack.post(link('validate'), [
                        validationStampName  : validationStamp,
                        validationRunStatusId: validationStampStatus,
                        description          : description
                ])
        )
    }

    @DSLMethod(id = "validate-closure", count = 3)
    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED', Closure closure) {
        def run = validate(validationStamp, validationStampStatus)
        run(closure)
        run
    }

    @DSLMethod(value = "Associates some data with the validation.", count = 4)
    ValidationRun validateWithData(String validationStamp, Object data, String dataType = null, String status = null) {
        new ValidationRun(
                ontrack,
                ontrack.post(
                        link("validate"),
                        [
                                validationStampData  : [
                                        id  : validationStamp,
                                        type: dataType,
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
                'net.nemerosa.ontrack.extension.general.validation.TextValidationDataType',
                status
        )
    }

    @DSLMethod(count = 6, value = """
        Associates some critical / high / medium / low issue counts with the validation. The
        validation stamp must be configured to accept CHML as validation data.""")
    ValidationRun validateWithCHML(String validationStamp, int critical = 0, int high = 0, int medium = 0, int low = 0, String status = null) {
        return validateWithData(validationStamp, [
                CRITICAL: critical,
                HIGH    : high,
                MEDIUM  : medium,
                LOW     : low,
        ], 'net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType', status)
    }

    @DSLMethod(count = 3, value = """
        Associates some number with the validation. The
        validation stamp must be configured to accept number as validation data.""")
    ValidationRun validateWithNumber(String validationStamp, int value, String status = null) {
        return validateWithData(
                validationStamp,
                [value: value],
                'net.nemerosa.ontrack.extension.general.validation.ThresholdNumberValidationDataType',
                status
        )
    }

    @DSLMethod(count = 3, value = """
        Associates some percentage with the validation. The
        validation stamp must be configured to accept percentage as validation data.""")
    ValidationRun validateWithPercentage(String validationStamp, int value, String status = null) {
        return validateWithData(
                validationStamp,
                [value: value],
                'net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType',
                status
        )
    }

    @DSLMethod(count = 4, value = """
        Associates some fraction with the validation. The
        validation stamp must be configured to accept fraction as validation data.""")
    ValidationRun validateWithFraction(String validationStamp, int numerator, int denominator, String status = null) {
        return validateWithData(
                validationStamp, [
                numerator  : numerator,
                denominator: denominator,
        ],
                'net.nemerosa.ontrack.extension.general.validation.FractionValidationDataType',
                status
        )
    }

    @DSLMethod(count = 4, value = """Associates some test results with the validation.""")
    ValidationRun validateWithTestSummary(String validationStamp, TestSummary testSummary, String status = null) {
        return validateWithData(
                validationStamp, [
                passed : testSummary.passed,
                skipped: testSummary.skipped,
                failed : testSummary.failed,
        ],
                'net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType',
                status
        )
    }

    @DSLMethod(count = 3, value = """Associates some arbitrary metrics with the validation.""")
    ValidationRun validateWithMetrics(String validationStamp, Map<String, Double> metrics, String status = null) {
        return validateWithData(
                validationStamp,
                [
                        metrics: metrics.collect { name, value ->
                            [
                                    name : name,
                                    value: value,
                            ]
                        }
                ],
                'net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType',
                status
        )
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
                        time: date ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date) : null
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
