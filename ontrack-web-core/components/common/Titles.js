export function projectTitle(project) {
    return title(project ? project.name : undefined)
}

export function branchTitle(branch) {
    return title(branch ? `${branch.name} @ ${branch.project.name}` : undefined)
}

export function subBranchTitle(branch, what) {
    return title(branch ? `${branch.name} @ ${branch.project.name} | ${what}` : undefined)
}

export function buildTitle(build) {
    return title(build ? `${build.branch.name}#${build.name} @ ${build.branch.project.name}` : undefined)
}

export function title(text) {
    return <title>{`Ontrack | ${text ? text : "loading..."}`}</title>
}