import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import PredefinedValidationStampCreateDialog, {
    usePredefinedValidationStampCreateDialog
} from "@components/core/config/PredefinedValidationStampCreateDialog";

export default function PredefinedValidationStampCreateCommand({onChange}) {

    const dialog = usePredefinedValidationStampCreateDialog({onChange})

    const onCreate = () => {
        dialog.start({})
    }

    return (
        <>
            <Command
                icon={<FaPlus/>}
                text={"Create predefined validation stamp"}
                action={onCreate}
            />
            <PredefinedValidationStampCreateDialog dialog={dialog}/>
        </>
    )
}