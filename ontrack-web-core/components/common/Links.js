import Link from "next/link";

const uiConfig = {
    // Production prefix
    prefix: '/ui',
};

if (process.env.NEXT_PUBLIC_LOCAL === 'true') {
    // Development prefix
    uiConfig.prefix = ''
}

export function homeUri() {
    return `${uiConfig.prefix}/`
}

export function homeLink(text) {
    return <Link href={homeUri()}>{text ? text : "Home"}</Link>
}

export function projectUri(project) {
    return `${uiConfig.prefix}/project/${project.id}`
}

export function projectLink(project, text) {
    return <Link href={projectUri(project)}>{text ? text : project.name}</Link>
}
