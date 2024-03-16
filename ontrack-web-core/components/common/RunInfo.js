import {Divider, Space} from "antd";
import RunInfoSource from "@components/common/RunInfoSource";
import RunInfoTime from "@components/common/RunInfoTime";

export default function RunInfo({info}) {
    return (
        <>
            <Space>
                <RunInfoSource info={info}/>
                <Divider type="vertical"/>
                <RunInfoTime info={info}/>
            </Space>
        </>
    )
}