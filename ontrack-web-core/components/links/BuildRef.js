import {Tooltip, Typography} from "antd";
import {buildLink} from "@components/common/Links";

export default function BuildRef({build}) {

    let text = <Typography.Text ellipsis>{build.name}</Typography.Text>
    if (build.releaseProperty?.value) {
        text = <Tooltip title={build.name}>{text}</Tooltip>
    }

    return buildLink(build, text)
}