import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import PredefinedValidationStampUpdateDialog, {
    usePredefinedValidationStampUpdateDialog
} from "@components/core/config/PredefinedValidationStampUpdateDialog";

export default function PredefinedValidationStampUpdateCommand({pvs, onChange}) {

    const dialog = usePredefinedValidationStampUpdateDialog({onChange})

    const onAction = () => {
        dialog.start({pvs})
    }

    return (
        <>
            <Command
                icon={<FaPencilAlt/>}
                title="Update"
                action={onAction}
            />
            <PredefinedValidationStampUpdateDialog dialog={dialog}/>
        </>
    )
}