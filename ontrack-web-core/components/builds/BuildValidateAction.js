import {Popover} from "antd";
import BuildValidateDialog, {useBuildValidateDialog} from "@components/builds/BuildValidateDialog";

export default function BuildValidateAction({build, validationStamp = undefined, children, onValidation}) {

    const dialog = useBuildValidateDialog({
        onSuccess: onValidation,
    })

    const onClick = () => {
        dialog.start({build, validationStamp})
    }

    return (
        <>
            <Popover content="Validates this build">
                <div onClick={onClick}>
                    {children}
                </div>
            </Popover>
            <BuildValidateDialog buildValidateDialog={dialog}/>
        </>
    )
}