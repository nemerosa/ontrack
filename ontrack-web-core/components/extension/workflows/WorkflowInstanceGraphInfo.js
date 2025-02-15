import {Typography} from "antd";
import WorkflowInstanceGraphInfoItems from "@components/extension/workflows/WorkflowInstanceGraphInfoItems";

export default function WorkflowInstanceGraphInfo({selectedNode}) {
    return (
        <>
            <div
                style={{
                    padding: '0.5em',
                }}
            >
                {
                    !selectedNode &&
                    <Typography.Text italic>Click on a node to display information.</Typography.Text>
                }
                {
                    selectedNode &&
                    <WorkflowInstanceGraphInfoItems
                        className="ot-line"
                        selectedNode={selectedNode}
                    />
                }
            </div>
        </>
    )
}