import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import CreateAccountGroupDialog, {
    useCreateAccountGroupDialog
} from "@components/core/admin/account-management/CreateAccountGroupDialog";

export default function CreateAccountGroupCommand({refresh}) {

    const dialog = useCreateAccountGroupDialog({refresh})

    const onCreate = async () => {
        dialog.start()
    }

    return (
        <>
            <Command
                icon={<FaPlus/>}
                title="Create a new account group"
                text="New group"
                action={onCreate}
            />
            <CreateAccountGroupDialog dialog={dialog}/>
        </>
    )
}