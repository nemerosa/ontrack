import Link from "next/link";
import clientConfig from "@client/clientConfig";
import {Space} from "antd";
import Image from "next/image";

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

export function buildUri(build) {
    return `${uiConfig.prefix}/build/${build.id}`
}

export function buildLink(build, text) {
    return <Link href={buildUri(build)}>{text ? text : build.name}</Link>
}

export function promotionLevelUri(promotionLevel) {
    return `${uiConfig.prefix}/promotionLevel/${promotionLevel.id}`
}

export function promotionLevelImageLink(promotionLevel) {
    return `${legacyUri()}rest/structure/promotionLevels/${promotionLevel.id}/image`
}

export const PromotionLevelImage = ({promotionLevel}) => {
    return (
        promotionLevel.image ?
            <Image
                src={promotionLevelImageLink(promotionLevel)}
                alt={promotionLevel.name}
                width={16}
                height={16}
            /> : undefined
    )
}

export function promotionLevelLink(promotionLevel, text) {
    return <Link href={promotionLevelUri(promotionLevel)}>
        <Space>
            <PromotionLevelImage promotionLevel={promotionLevel}/>
            {text ? text : promotionLevel.name}
        </Space>
    </Link>
}
