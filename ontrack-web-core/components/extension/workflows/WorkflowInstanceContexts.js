import {Space} from "antd";
import WorkflowInstanceContext from "@components/extension/workflows/WorkflowInstanceContext";

export default function WorkflowInstanceContexts({contexts}) {
    return (
        <>
            <Space>
                {
                    contexts.map((context, index) => (
                        <>
                            <WorkflowInstanceContext key={index} context={context}/>
                        </>
                    ))
                }
            </Space>
        </>
    )
}