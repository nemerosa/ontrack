/**
 * Trigger job
 */

job {
    name 'ontrack-2-trigger'
}

/**
 * Commit phase
 */

job {
    name 'ontrack-2-quick'
}

job {
    name 'ontrack-2-package'
}

/**
 * Acceptance phase
 */

job {
    name 'ontrack-2-acceptance-local'
}

job {
    name 'ontrack-2-docker'
}

job {
    name 'ontrack-2-acceptance-do'
}

/**
 * Release phase
 */

job {
    name 'ontrack-2-publish'
}

job {
    name 'ontrack-2-production'
}
