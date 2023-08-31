export function projectTitle(project) {
    return title(project.name)
}

export function branchTitle(branch) {
    return title(`${branch.name} @ ${branch.project.name}`)
}

export function buildTitle(build) {
    return title(`${build.branch.name}#${build.name} @ ${build.branch.project.name}`)
}

export function title(text) {
    return <title>Ontrack | {text}</title>
}