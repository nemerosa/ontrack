export function projectTitle(project) {
    return title(project.name)
}

function title(text) {
    return <title>Ontrack | {text}</title>
}