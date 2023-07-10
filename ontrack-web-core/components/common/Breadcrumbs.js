import {homeLink, projectLink} from "@components/common/Links";

export function homeBreadcrumbs() {
    return [
        homeLink(),
    ]
}

export function projectBreadcrumbs(project) {
    return [
        homeLink(),
    ]
}

export function branchBreadcrumbs(branch) {
    return [
        homeLink(),
        projectLink(branch.project),
    ]
}