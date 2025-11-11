import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import ValidationStampUpdateDialog, {
    useValidationStampUpdateDialog
} from "@components/validationStamps/ValidationStampUpdateDialog";

export default function ValidationStampUpdateCommand({validationStamp, refresh}) {

    const validationStampUpdateDialog = useValidationStampUpdateDialog({onSuccess: refresh})

    const onAction = () => {
        validationStampUpdateDialog.start({validationStamp})
    }

    return (
        <>
            <Command
                icon={<FaPencilAlt/>}
                text="Update validation stamp"
                action={onAction}
            />
            <ValidationStampUpdateDialog validationStampUpdateDialog={validationStampUpdateDialog}/>
        </>
    )
}