export function projectTitle(project) {
    return title(project ? project.name : undefined)
}

export function branchTitle(branch) {
    return title(branch ? `${branch.name} / ${branch.project.name}` : undefined)
}

export function subBranchTitleName(branch, what) {
    return what ? (branch ? `${what} | ${branch.name} / ${branch.project.name}` : undefined) : ''
}

export function subBranchTitle(branch, what) {
    return title(subBranchTitleName(branch, what))
}

export function subBuildTitle(build, what) {
    return title(build ? `${what} | ${buildKnownName(build)} / ${build.branch.project.name}` : '')
}

export function buildKnownName(build) {
    return build?.releaseProperty?.value?.name ?? build?.name
}

export function buildLinkName({qualifier, build}) {
    if (qualifier) {
        return `${buildKnownName(build)}@${build.branch.project.name} (${qualifier})`
    } else {
        return `${buildKnownName(build)}@${build.branch.project.name}`
    }
}

export function buildTitle(build) {
    return subBranchTitle(build.branch, buildKnownName(build))
}

export function promotionLevelTitleName(promotionLevel, what) {
    const name = subBranchTitleName(promotionLevel.branch, promotionLevel.name)
    return what ? `${name} | ${what}` : name
}

export function promotionLevelTitle(promotionLevel) {
    return title(promotionLevelTitleName(promotionLevel))
}

export function validationStampTitle(validationStamp) {
    return subBranchTitle(validationStamp.branch, validationStamp.name)
}

export function title(text) {
    return <title>{`Ontrack | ${text ? text : "loading..."}`}</title>
}

export const pageTitle = title
