import {CountTag} from "@components/framework/validation-run-data/CountTag";
import {Space} from "antd";

export default function CHMLValidationDataType({levels}) {
    return (
        <Space size={0}>
            <CountTag count={levels.CRITICAL} color="error" title="# of critical issues"/>
            <CountTag count={levels.HIGH} color="warning" title="# of high severity issues"/>
            <CountTag count={levels.MEDIUM} color="blue" title="# of medium severity issues"/>
            <CountTag count={levels.LOW} color="default" title="# of low severity issues"/>
        </Space>
    )
}