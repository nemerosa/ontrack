import {Command} from "@components/common/Commands";
import {FaImage} from "react-icons/fa";
import PredefinedValidationStampChangeImageDialog, {
    usePredefinedValidationStampChangeImageDialog
} from "@components/core/config/PredefinedValidationStampChangeImageDialog";

export default function PredefinedValidationStampChangeImageCommand({id, onChange}) {

    const dialog = usePredefinedValidationStampChangeImageDialog({onChange})

    const onChangeImage = () => {
        dialog.start({id: Number(id)})
    }

    return (
        <>
            <Command
                icon={<FaImage/>}
                title="Change image"
                action={onChangeImage}
            />
            <PredefinedValidationStampChangeImageDialog dialog={dialog}/>
        </>
    )
}