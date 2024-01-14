import {buildLink} from "@components/common/Links";
import {buildKnownName} from "@components/common/Titles";

export default function BuildLink({build, text}) {
    return buildLink(build, text ?? buildKnownName(build))
}