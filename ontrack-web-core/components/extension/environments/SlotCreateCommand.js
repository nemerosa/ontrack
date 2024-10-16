import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import NewSlotDialog, {useNewSlotDialog} from "@components/extension/environments/NewSlotDialog";

export default function SlotCreateCommand() {
    const user = useContext(UserContext)

    const dialog = useNewSlotDialog()

    const createSlot = () => {
        dialog.start({})
    }

    return (
        <>
            {
                user.authorizations.environment?.view && <>
                    <Command
                        icon={<FaPlus/>}
                        text="New slot"
                        title="Create a new slot"
                        action={createSlot}
                    />
                    <NewSlotDialog newSlotDialog={dialog}/>
                </>
            }
        </>
    )
}