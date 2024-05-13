import {Button, Space} from "antd";
import {FaProjectDiagram} from "react-icons/fa";
import EditWorkflowDialog, {useEditWorkflowDialog} from "@components/extension/workflows/EditWorkflowDialog";
import {ReactFlowProvider} from "reactflow";

export default function EditWorkflowButton({value, onChange}) {

    const dialog = useEditWorkflowDialog({
        onSuccess: onChange
    })

    const startEdition = () => {
        dialog.start(value)
    }

    return (
        <>
            <Space>
                <Button size="small" icon={<FaProjectDiagram/>} onClick={startEdition}>Edit workflow</Button>
                {value?.name}
            </Space>
            <ReactFlowProvider>
                <EditWorkflowDialog dialog={dialog}/>
            </ReactFlowProvider>
        </>
    )
}