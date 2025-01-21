import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import PredefinedPromotionLevelCreateDialog, {
    usePredefinedPromotionLevelCreateDialog
} from "@components/core/config/PredefinedPromotionLevelCreateDialog";

export default function PredefinedPromotionLevelCreateCommand({onChange}) {

    const dialog = usePredefinedPromotionLevelCreateDialog({onChange})

    const onCreate = () => {
        dialog.start({})
    }

    return (
        <>
            <Command
                icon={<FaPlus/>}
                text={"Create predefined promotion level"}
                action={onCreate}
            />
            <PredefinedPromotionLevelCreateDialog dialog={dialog}/>
        </>
    )
}