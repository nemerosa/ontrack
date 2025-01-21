import {Command} from "@components/common/Commands";
import {FaImage} from "react-icons/fa";
import PredefinedPromotionLevelChangeImageDialog, {
    usePredefinedPromotionLevelChangeImageDialog
} from "@components/core/config/PredefinedPromotionLevelChangeImageDialog";

export default function PredefinedPromotionLevelChangeImageCommand({id, onChange}) {

    const dialog = usePredefinedPromotionLevelChangeImageDialog({onChange})

    const onChangeImage = () => {
        dialog.start({id})
    }

    return (
        <>
            <Command
                icon={<FaImage/>}
                title="Change image"
                action={onChangeImage}
            />
            <PredefinedPromotionLevelChangeImageDialog dialog={dialog}/>
        </>
    )
}