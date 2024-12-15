import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";
import HomeLink from "@components/common/HomeLink";
import BuildLink from "@components/builds/BuildLink";
import Link from "next/link";
import {branchPromotionLevelsUri} from "@components/common/Links";

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

export function downToBranchBreadcrumbs({branch, following = []}) {
    return [
        <HomeLink key="home"/>,
        <ProjectLink project={branch.project} key="project"/>,
        <BranchLink branch={branch} key="branch"/>,
        ...following,
    ]
}

export function buildBreadcrumbs(build) {
    return downToBranchBreadcrumbs(build)
}

export function downToBuildBreadcrumbs({build}) {
    const crumbs = [
        <HomeLink key="home"/>
    ]
    if (build) {
        crumbs.push(
            <ProjectLink project={build.branch.project} key="project"/>,
            <BranchLink branch={build.branch} key="branch"/>,
            <BuildLink build={build} key="build"/>
        )
    }
    return crumbs
}

export function promotionLevelBreadcrumbs(promotionLevel) {
    return downToBranchBreadcrumbs({
        branch: promotionLevel.branch,
        following: [
            <Link key="promotions" href={branchPromotionLevelsUri(promotionLevel.branch)}>Promotion levels</Link>,
        ],
    })
}

export function validationStampBreadcrumbs(validationStamp) {
    return validationStamp ? downToBranchBreadcrumbs(validationStamp) : []
}