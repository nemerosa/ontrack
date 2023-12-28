import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import PromotionLevelUpdateDialog, {usePromotionLevelUpdateDialog} from "@components/promotionLevels/PromotionLevelUpdateDialog";

export default function PromotionLevelUpdateCommand({id}) {

    const promotionLevelUpdateDialog = usePromotionLevelUpdateDialog()

    const onAction = () => {
        promotionLevelUpdateDialog.start({id})
    }

    return (
        <>
            <Command
                icon={<FaPencilAlt/>}
                text="Update promotion level"
                action={onAction}
            />
            <PromotionLevelUpdateDialog promotionLevelUpdateDialog={promotionLevelUpdateDialog}/>
        </>
    )
}