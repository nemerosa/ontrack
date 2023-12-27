import {Command} from "@components/common/Commands";
import {FaImage} from "react-icons/fa";
import PromotionLevelChangeImageDialog, {
    usePromotionLevelChangeImageDialog
} from "@components/promotionLevels/PromotionLevelChangeImageDialog";

export default function PromotionLevelChangeImageCommand({id}) {

    const promotionLevelChangeImageDialog = usePromotionLevelChangeImageDialog()

    const onChangeImage = () => {
        promotionLevelChangeImageDialog.start({id})
    }

    return (
        <>
            <Command
                icon={<FaImage/>}
                text="Change image"
                action={onChangeImage}
            />
            <PromotionLevelChangeImageDialog promotionLevelChangeImageDialog={promotionLevelChangeImageDialog}/>
        </>
    )
}