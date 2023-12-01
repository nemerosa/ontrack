import Link from "next/link";

/**
 * @deprecated Use the LegacyLink component
 */
export function legacyUri() {
    return ''
    // const config = clientConfig()
    // return `${config.url}/`
}

export function legacyGraphiQLUri() {
    return `/graphiql.html`
}

export function homeUri() {
    return `/`
}

export function projectUri(project) {
    return `/project/${project.id}`
}

export function legacyProjectUri(project) {
    return `#/project/${project.id}`
}

export function branchUri(branch) {
    return `/branch/${branch.id}`
}

export function branchLegacyUri(branch) {
    return `#/branch/${branch.id}`
}

export function buildUri(build) {
    return `/build/${build.id}`
}

export function buildLegacyUri(build) {
    return `#/build/${build.id}`
}

// TODO As a component
export function buildLink(build, text) {
    return <Link
        href={buildUri(build)}
        title="Link to build page"
    >{text ? text : build.name}</Link>
}

export function legacyPromotionLevelUri(promotionLevel) {
    return `#/promotionLevel/${promotionLevel.id}`
}

export function legacyPromotionLevelImageUri(promotionLevel) {
    return `/rest/structure/promotionLevels/${promotionLevel.id}/image`
}

export function legacyPredefinedPromotionLevelImageUri(predefinedPromotionLevel) {
    return `/rest/admin/predefinedPromotionLevels/${predefinedPromotionLevel.id}/image`
}

export function legacyPredefinedValidationStampImageUri(predefinedValidationStamp) {
    return `/rest/admin/predefinedValidationStamps/${predefinedValidationStamp.id}/image`
}

export function legacyValidationStampImageUri(validationStamp) {
    return `/rest/structure/validationStamps/${validationStamp.id}/image`
}

export function legacyValidationStampUri(validationStamp) {
    return `#/validationStamp/${validationStamp.id}`
}

export function legacyValidationRunUri(validationRun) {
    return `#/validationRun/${validationRun.id}`
}

export function legacyGitChangeLogUri({from, to}) {
    return `#/extension/git/changelog?from=${from}&to=${to}`
}
