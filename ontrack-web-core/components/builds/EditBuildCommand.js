import {Command} from "@components/common/Commands";
import {FaPencilAlt} from "react-icons/fa";
import {isAuthorized} from "@components/common/authorizations";
import EditBuildDialog, {useEditBuildDialog} from "@components/builds/EditBuildDialog";

export default function EditBuildCommand({build, onSuccess}) {

    const dialog = useEditBuildDialog({onSuccess})

    const onClick = () => {
        dialog.start({build})
    }

    return (
        <>
            {
                isAuthorized(build, "build", "edit") &&
                <>
                    <Command
                        title="Edit the build name and description"
                        icon={<FaPencilAlt/>}
                        text="Edit"
                        action={onClick}
                    />
                    <EditBuildDialog dialog={dialog}/>
                </>
            }
        </>
    )
}