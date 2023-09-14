import {buildLink} from "@components/common/Links";

export default function BuildLink({build}) {
    return buildLink(build, build.releaseProperty?.value ? build.releaseProperty?.value : build.name)
}