import Link from "next/link";
import clientConfig from "@client/clientConfig";
import {Space} from "antd";
import Image from "next/image";
import LegacyLink from "@components/common/LegacyLink";

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
    return `${legacyUri()}#/promotionLevel/1532`
    // TODO LEGACY return `${uiConfig.prefix}/promotionLevel/${promotionLevel.id}`
}

export function promotionLevelImageUri(promotionLevel) {
    return `${legacyUri()}rest/structure/promotionLevels/${promotionLevel.id}/image`
}

export function predefinedPromotionLevelImageUri(predefinedPromotionLevel) {
    return `${legacyUri()}rest/admin/predefinedPromotionLevels/${predefinedPromotionLevel.id}/image`
}

export const PromotionLevelImage = ({promotionLevel, size = 16}) => {
    return (
        promotionLevel.image ?
            <Image
                src={promotionLevelImageUri(promotionLevel)}
                alt={promotionLevel.name}
                width={size}
                height={size}
            /> : undefined
    )
}

export function promotionLevelLink(promotionLevel, text) {
    return <LegacyLink href={promotionLevelUri(promotionLevel)}>
        <Space>
            <PromotionLevelImage promotionLevel={promotionLevel}/>
            {text ? text : promotionLevel.name}
        </Space>
    </LegacyLink>
}

export function predefinedValidationStampImageUri(predefinedValidationStamp) {
    return `${legacyUri()}rest/admin/predefinedValidationStamps/${predefinedValidationStamp.id}/image`
}

export function validationStampImageUri(validationStamp) {
    return `${legacyUri()}rest/structure/validationStamps/${validationStamp.id}/image`
}

export function validationStampUri(validationStamp) {
    return `${uiConfig.prefix}/validationStamp/${validationStamp.id}`
}

export const ValidationStampImage = ({validationStamp}) => {
    return (
        validationStamp.image ?
            <Image
                src={validationStampImageUri(validationStamp)}
                alt={validationStamp.name}
                width={16}
                height={16}
            /> : undefined
    )
}

export function validationStampLink(validationStamp, text) {
    return <Link href={validationStampUri(validationStamp)}>
        <Space>
            <ValidationStampImage validationStamp={validationStamp}/>
            {text ? text : validationStamp.name}
        </Space>
    </Link>
}
