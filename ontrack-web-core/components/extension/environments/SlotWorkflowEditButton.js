import {FaPencilAlt} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import SlotWorkflowDialog, {useSlotWorkflowDialog} from "@components/extension/environments/SlotWorkflowDialog";

export default function SlotWorkflowEditButton({slot, slotWorkflow, onChange}) {

    const dialog = useSlotWorkflowDialog({
        onSuccess: onChange,
    })

    const onEdit = async () => {
        dialog.start({slot, slotWorkflow})
    }

    return (
        <>
            <SlotWorkflowDialog dialog={dialog}/>
            <Command
                icon={<FaPencilAlt/>}
                title="Edit slot workflow"
                action={onEdit}
            />
        </>
    )
}