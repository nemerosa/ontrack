export function projectTitle(project) {
    return title(project ? project.name : undefined)
}

export function branchTitle(branch) {
    return title(branch ? `${branch.name} / ${branch.project.name}` : undefined)
}

export function subBranchTitle(branch, what) {
    return title(branch ? `${what} | ${branch.name} / ${branch.project.name}` : undefined)
}

export function buildTitle(build) {
    return subBranchTitle(build.branch, build.name)
}

export function promotionLevelTitle(promotionLevel) {
    return subBranchTitle(promotionLevel.branch, promotionLevel.name)
}

export function title(text) {
    return <title>{`Ontrack | ${text ? text : "loading..."}`}</title>
}