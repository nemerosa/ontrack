import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";
import HomeLink from "@components/common/HomeLink";

export function homeBreadcrumbs() {
    return [
        <HomeLink/>,
    ]
}

export function projectBreadcrumbs() {
    return [
        <HomeLink/>,
    ]
}

export function branchBreadcrumbs(branch) {
    return [
        <HomeLink/>,
        <ProjectLink project={branch.project}/>,
    ]
}

export function downToBranchBreeadcrumbs({branch}) {
    return [
        <HomeLink/>,
        <ProjectLink project={branch.project}/>,
        <BranchLink branch={branch}/>,
    ]
}

export function buildBreadcrumbs(build) {
    return downToBranchBreeadcrumbs(build)
}