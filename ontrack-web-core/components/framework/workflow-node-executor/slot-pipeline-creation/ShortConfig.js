import {Typography} from "antd";

export default function SlotPipelineCreationWorkflowNodeExecutorShortConfig({data}) {

    const {environment, qualifier} = data

    return (
        <>
            <Typography.Text>{environment}</Typography.Text>
            {
                qualifier &&
                <Typography.Text type="secondary">[{qualifier}]</Typography.Text>
            }
        </>
    )

}