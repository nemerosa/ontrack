import Link from "next/link";

const prefix = '';

export function homeUri() {
    return `${prefix}/`
}

export function homeLink(text) {
    return <Link href={homeUri()}>{text ? text : "Home"}</Link>
}

export function projectUri(project) {
    return `${prefix}/project/${project.id}`
}

export function projectLink(project, text) {
    return <Link href={projectUri(project)}>{text ? text : project.name}</Link>
}
