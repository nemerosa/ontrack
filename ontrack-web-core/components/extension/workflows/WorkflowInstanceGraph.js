import {ReactFlowProvider} from "reactflow";
import {WorkflowInstanceGraphFlow} from "@components/extension/workflows/WorkflowInstanceGraphFlow";
import {Col, Row} from "antd";
import WorkflowInstanceGraphInfo from "@components/extension/workflows/WorkflowInstanceGraphInfo";
import {useState} from "react";

export default function WorkflowInstanceGraph({instance}) {

    const [nodeSelected, setNodeSelected] = useState()

    return (
        <>
            <Row>
                <Col span={16}>
                    <ReactFlowProvider>
                        <WorkflowInstanceGraphFlow instance={instance} onNodeSelected={setNodeSelected}/>
                    </ReactFlowProvider>
                </Col>
                <Col span={8}>
                    <WorkflowInstanceGraphInfo instance={instance} selectedNode={nodeSelected}/>
                </Col>
            </Row>
        </>
    )
}

