import {Command} from "@components/common/Commands";
import {FaImage} from "react-icons/fa";
import ValidationStampChangeImageDialog, {
    useValidationStampChangeImageDialog
} from "@components/validationStamps/ValidationStampChangeImageDialog";

export default function ValidationStampChangeImageCommand({id}) {

    const validationStampChangeImageDialog = useValidationStampChangeImageDialog()

    const onChangeImage = () => {
        validationStampChangeImageDialog.start({id})
    }

    return (
        <>
            <Command
                icon={<FaImage/>}
                text="Change image"
                action={onChangeImage}
            />
            <ValidationStampChangeImageDialog validationStampChangeImageDialog={validationStampChangeImageDialog}/>
        </>
    )
}