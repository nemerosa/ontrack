import {Button} from "antd";
import {FaExclamationCircle} from "react-icons/fa";
import SlotPipelineOverrideWorkflowDialog, {
    useSlotPipelineOverrideWorkflowDialog
} from "@components/extension/environments/SlotPipelineOverrideWorkflowDialog";

export default function SlotPipelineOverrideWorkflowButton({deployment, slotWorkflow, slotWorkflowInstance, onChange}) {

    const dialog = useSlotPipelineOverrideWorkflowDialog({
        onSuccess: onChange,
    })

    const onOverride = () => {
        dialog.start({deployment, slotWorkflow})
    }

    return (
        <>
            {
                !slotWorkflowInstance.check.ok && !slotWorkflowInstance.overridden && slotWorkflowInstance.canBeOverridden && <>
                    <Button
                        title="Overriding the result of this workflow"
                        data-testid={`override-${slotWorkflow.id}`}
                        onClick={onOverride}
                    >
                        <FaExclamationCircle color="red"/>
                    </Button>
                    <SlotPipelineOverrideWorkflowDialog dialog={dialog}/>
                </>
            }
        </>
    )
}