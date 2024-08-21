import {Divider, Space} from "antd";
import RunInfoSource from "@components/common/RunInfoSource";
import RunInfoTime from "@components/common/RunInfoTime";

export default function RunInfo({info, mode = "complete"}) {
    return (
        <>
            <Space size={mode === "complete" ? 4 : 1}>
                <RunInfoSource info={info} mode={mode}/>
                <Divider type="vertical"/>
                <RunInfoTime info={info} mode={mode}/>
            </Space>
        </>
    )
}