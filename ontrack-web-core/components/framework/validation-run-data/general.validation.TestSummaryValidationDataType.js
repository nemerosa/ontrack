import {Space} from "antd";
import {CountTag} from "@components/framework/validation-run-data/CountTag";

export default function TestSummaryValidationDataType({passed, skipped, failed, total}) {
    return (
        <Space size={0}>
            <CountTag count={passed} color="success" title="# of passed tests"/>
            <CountTag count={skipped} color="warning" title="# of skipped tests"/>
            <CountTag count={failed} color="red" title="# of failed tests"/>
            <CountTag count={total} color="default" title="Total # of tests"/>
        </Space>
    )
}