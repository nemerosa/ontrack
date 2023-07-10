export function projectTitle(project) {
    return title(project.name)
}

export function branchTitle(branch) {
    return title(branch.name)
}

export function title(text) {
    return <title>Ontrack | {text}</title>
}