import Link from "next/link";
import clientConfig from "@client/clientConfig";

const uiConfig = {
    prefix: '',
};

export function legacyUri() {
    const config = clientConfig()
    return `${config.url}/`
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

export function branchUri(branch) {
    return `${uiConfig.prefix}/branch/${branch.id}`
}

export function branchLink(branch, text) {
    return <Link href={branchUri(branch)}>{text ? text : branch.name}</Link>
}
