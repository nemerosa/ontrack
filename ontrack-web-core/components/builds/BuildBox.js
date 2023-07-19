import {Space} from "antd";
import {buildLink} from "@components/common/Links";
import Decorations from "@components/framework/decorations/Decorations";
import Timestamp from "@components/common/Timestamp";

export default function BuildBox({build}) {
    return (
        <Space direction="vertical">
            <Space>
                { buildLink(build) }
                <Decorations entity={build}/>
            </Space>
            <Timestamp value={build.creation.time}/>
        </Space>
    )
}