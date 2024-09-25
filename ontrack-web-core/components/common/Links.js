import Link from "next/link";

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

export function branchLinksUri(branch) {
    return `/branch/${branch.id}/links`
}

export function branchPromotionLevelsUri(branch) {
    return `/branch/${branch.id}/promotionLevels`
}

export function branchAutoVersioningUri(branch) {
    return `/extension/auto-versioning/config/${branch.id}`
}

export function branchLegacyUri(branch) {
    return `#/branch/${branch.id}`
}

export function buildUri(build) {
    return `/build/${build.id}`
}

export function buildLinksUri(build) {
    return `/build/${build.id}/links`
}

export function buildLegacyUri(build) {
    return `#/build/${build.id}`
}

export function scmChangeLogUri(from, to) {
    return `/extension/scm/changelog?from=${from}&to=${to}`
}

export function autoVersioningAuditEntryUri(uuid) {
    return `/extension/auto-versioning/audit/detail/${uuid}`
}

// TODO As a component
export function buildLink(build, text) {
    return <Link
        href={buildUri(build)}
        title="Link to build page"
    >{text ? text : build.name}</Link>
}

export function promotionLevelUri(promotionLevel) {
    return `/promotionLevel/${promotionLevel.id}`
}

export function promotionRunUri(promotionRun) {
    return `/promotionRun/${promotionRun.id}`
}

export function validationStampUri(validationStamp) {
    return `/validationStamp/${validationStamp.id}`
}

export function validationRunUri(validationRun) {
    return `/validationRun/${validationRun.id}`
}

export function restPromotionLevelImageUri(promotionLevel) {
    return `/rest/structure/promotionLevels/${promotionLevel.id}/image`
}

export function restPredefinedPromotionLevelImageUri(predefinedPromotionLevel) {
    return `/rest/admin/predefinedPromotionLevels/${predefinedPromotionLevel.id}/image`
}

export function restPredefinedValidationStampImageUri(predefinedValidationStamp) {
    return `/rest/admin/predefinedValidationStamps/${predefinedValidationStamp.id}/image`
}

export function restValidationStampImageUri(validationStamp) {
    return `/rest/structure/validationStamps/${validationStamp.id}/image`
}

export function legacyValidationRunUri(validationRun) {
    return `#/validationRun/${validationRun.id}`
}
