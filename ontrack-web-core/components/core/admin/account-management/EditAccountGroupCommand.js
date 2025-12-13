import {Command} from "@components/common/Commands";
import {FaPencil} from "react-icons/fa6";
import EditAccountGroupDialog, {
    useEditAccountGroupDialog
} from "@components/core/admin/account-management/EditAccountGroupDialog";

export default function EditAccountGroupCommand({accountGroup, refresh}) {

    const dialog = useEditAccountGroupDialog({refresh})

    const onEdit = () => {
        dialog.start({accountGroup})
    }

    return (
        <>
            <Command
                icon={<FaPencil/>}
                title="Edit details of this account group"
                action={onEdit}
            />
            <EditAccountGroupDialog dialog={dialog}/>
        </>
    )
}