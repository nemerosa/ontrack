import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import PromotionLevelCreateDialog, {
    usePromotionLevelCreateDialog
} from "@components/promotionLevels/PromotionLevelCreateDialog";

export default function PromotionLevelCreateCommand({branch}) {

    const dialog = usePromotionLevelCreateDialog()

    const onClick = () => {
        dialog.start({branch})
    }

    return (
        <>
            <Command
                icon={<FaPlus/>}
                text="Create promotion level"
                action={onClick}
            />
            <PromotionLevelCreateDialog dialog={dialog}/>
        </>
    )
}