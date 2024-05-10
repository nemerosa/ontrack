import {Button} from "antd";
import {FaProjectDiagram} from "react-icons/fa";
import EditWorkflowDialog, {useEditWorkflowDialog} from "@components/extension/workflows/EditWorkflowDialog";
import {ReactFlowProvider} from "reactflow";

export default function EditWorkflowButton({value, onChange}) {

    const dialog = useEditWorkflowDialog()

    const startEdition = () => {
        dialog.start(value)
    }

    return (
        <>
            <Button size="small" icon={<FaProjectDiagram/>} onClick={startEdition}>Edit workflow</Button>
            <ReactFlowProvider>
                <EditWorkflowDialog dialog={dialog}/>
            </ReactFlowProvider>
        </>
    )
}