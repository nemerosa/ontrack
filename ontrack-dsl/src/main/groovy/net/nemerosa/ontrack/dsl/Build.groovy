package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.BuildProperties

class Build extends AbstractProjectResource {

    Build(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    String getProject() {
        node?.branch?.project?.name
    }

    String getBranch() {
        node?.branch?.name
    }

    PromotionRun promote(String promotion) {
        new PromotionRun(
                ontrack,
                ontrack.post(link('promote'), [
                        promotionLevelName: promotion,
			            description : '',
                ])
        )
    }

    PromotionRun promote(String promotion, Closure closure) {
        def run = promote(promotion)
        run(closure)
        run
    }

    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED') {
        new ValidationRun(
                ontrack,
                ontrack.post(link('validate'), [
                        validationStampName  : validationStamp,
                        validationRunStatusId: validationStampStatus,
			description : ''
                ])
        )
    }

    ValidationRun validate(String validationStamp, String validationStampStatus = 'PASSED', Closure closure) {
        def run = validate(validationStamp, validationStampStatus)
        run(closure)
        run
    }

    List<PromotionRun> getPromotionRuns() {
        ontrack.get(link('promotionRuns')).resources.collect {
            new PromotionRun(ontrack, it)
        }
    }

    List<ValidationRun> getValidationRuns() {
        ontrack.get(link('validationRuns')).resources.collect {
            new ValidationRun(ontrack, it)
        }
    }

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
     * SVN revision decoration
     */
    Long getSvnRevisionDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.svn.SVNRevisionDecorationExtension') as Long
    }

}
