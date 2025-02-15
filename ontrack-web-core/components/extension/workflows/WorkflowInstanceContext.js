import {Dynamic} from "@components/common/Dynamic";
import {Space, Tag} from "antd";

export default function WorkflowInstanceContext({context}) {
    return (
        <>
            <Space size={4}>
                <Tag>{context.name}</Tag>
                <Dynamic
                    path={`framework/templating-context/${context.contextData.id}/Component`}
                    props={{...context.contextData.data}}
                />
            </Space>
        </>
    )
}