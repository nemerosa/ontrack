import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";
import HomeLink from "@components/common/HomeLink";
import BuildLink from "@components/builds/BuildLink";

export function homeBreadcrumbs() {
    return [
        <HomeLink key="home"/>,
    ]
}

export function projectBreadcrumbs() {
    return [
        <HomeLink key="home"/>,
    ]
}

export function downToProjectBreadcrumbs({project}) {
    return [
        <HomeLink key="home"/>,
        <ProjectLink project={project} key="project"/>,
    ]
}

export function branchBreadcrumbs(branch) {
    return downToProjectBreadcrumbs(branch)
}

export function downToBranchBreadcrumbs({branch}) {
    return [
        <HomeLink key="home"/>,
        <ProjectLink project={branch.project} key="project"/>,
        <BranchLink branch={branch} key="branch"/>,
    ]
}

export function buildBreadcrumbs(build) {
    return downToBranchBreadcrumbs(build)
}

export function downToBuildBreadcrumbs({build}) {
    return [
        <HomeLink key="home"/>,
        <ProjectLink project={build.branch.project} key="project"/>,
        <BranchLink branch={build.branch} key="branch"/>,
        <BuildLink build={build} key="build"/>
    ]
}

export function promotionLevelBreadcrumbs(promotionLevel) {
    return downToBranchBreadcrumbs(promotionLevel)
}

export function validationStampBreadcrumbs(validationStamp) {
    return downToBranchBreadcrumbs(validationStamp)
}