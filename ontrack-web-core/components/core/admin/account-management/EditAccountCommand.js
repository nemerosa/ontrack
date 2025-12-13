import {Command} from "@components/common/Commands";
import {FaPencil} from "react-icons/fa6";
import EditAccountDialog, {useEditAccountDialog} from "@components/core/admin/account-management/EditAccountDialog";

export default function EditAccountCommand({account, refresh}) {

    const dialog = useEditAccountDialog({refresh})

    const onEdit = () => {
        dialog.start({account})
    }

    return (
        <>
            <Command
                icon={<FaPencil/>}
                title="Edit details of this account"
                action={onEdit}
            />
            <EditAccountDialog dialog={dialog}/>
        </>
    )
}