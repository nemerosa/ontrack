import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import PromotionLevelUpdateDialog, {usePromotionLevelUpdateDialog} from "@components/promotionLevels/PromotionLevelUpdateDialog";

export default function ValidationStampUpdateCommand({id}) {

    const validationStampUpdateDialog = useValidationStampUpdateDialog()

    const onAction = () => {
        validationStampUpdateDialog.start({id})
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