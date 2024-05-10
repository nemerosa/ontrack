import {Button} from "antd";
import {FaSearch} from "react-icons/fa";
import ShowWorkflowDialog, {useShowWorkflowDialog} from "@components/extension/workflows/ShowWorkflowDialog";

export default function WorkflowNotificationChannelConfig({workflow}) {

    const dialog = useShowWorkflowDialog()

    const showWorkflow = () => {
        dialog.start(workflow)
    }

    return (
        <>
            <Button onClick={showWorkflow} icon={<FaSearch/>} size="small">Show workflow</Button>
            <ShowWorkflowDialog dialog={dialog}/>
        </>
    )
}