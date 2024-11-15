import ShowWorkflowDialog, {useShowWorkflowDialog} from "@components/extension/workflows/ShowWorkflowDialog";
import {Button, Space} from "antd";
import {FaProjectDiagram} from "react-icons/fa";

export default function ShowWorkflowButton({workflow}) {

    const dialog = useShowWorkflowDialog()

    const showWorkflow = () => {
        dialog.start(workflow)
    }

    return (
        <>
            <Space>
                <Button onClick={showWorkflow} icon={<FaProjectDiagram/>} size="small">Show workflow</Button>
                {workflow?.name}
            </Space>
            <ShowWorkflowDialog dialog={dialog}/>
        </>
    )
}