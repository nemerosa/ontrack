export function projectTitleName(project, what) {
    return project ? (what ? `${project.name} | ${what}` : project.name) : undefined
}

export function projectTitle(project, what) {
    return title(projectTitleName(project, what))
}

export function branchTitleName(branch, what) {
    if (!branch) return undefined
    const name = `${branch.name} / ${branch.project.name}`
    return what ? `${name} | ${what}` : name
}

export function branchTitle(branch) {
    return title(branchTitleName(branch))
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
    return build?.displayName ?? build?.releaseProperty?.value?.name ?? build?.name
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

export function validationStampTitleName(validationStamp, what) {
    const name = subBranchTitleName(validationStamp.branch, validationStamp.name)
    return what ? `${name} | ${what}` : name
}

export function validationStampTitle(validationStamp) {
    return validationStamp ? title(validationStampTitleName(validationStamp)) : ''
}

export function title(text) {
    return <title>{`Yontrack - ${text ? text : "loading..."}`}</title>
}

export const pageTitle = title
