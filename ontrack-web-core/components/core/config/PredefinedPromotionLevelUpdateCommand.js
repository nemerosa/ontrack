import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import PredefinedPromotionLevelUpdateDialog, {
    usePredefinedPromotionLevelUpdateDialog
} from "@components/core/config/PredefinedPromotionLevelUpdateDialog";

export default function PredefinedPromotionLevelUpdateCommand({ppl, onChange}) {

    const dialog = usePredefinedPromotionLevelUpdateDialog({onChange})

    const onAction = () => {
        dialog.start({ppl})
    }

    return (
        <>
            <Command
                icon={<FaPencilAlt/>}
                title="Update"
                action={onAction}
            />
            <PredefinedPromotionLevelUpdateDialog dialog={dialog}/>
        </>
    )
}