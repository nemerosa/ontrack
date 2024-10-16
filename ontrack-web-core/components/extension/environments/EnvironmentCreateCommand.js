import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import NewEnvironmentDialog, {useNewEnvironmentDialog} from "@components/extension/environments/NewEnvironmentDialog";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";

export default function EnvironmentCreateCommand() {

    const dialog = useNewEnvironmentDialog()
    const user = useContext(UserContext)

    const createEnvironment = () => {
        dialog.start({})
    }

    return (
        <>
            {
                user.authorizations.environment?.create && <>
                    <Command
                        icon={<FaPlus/>}
                        text="New environment"
                        title="Create a new environment"
                        action={createEnvironment}
                    />
                    <NewEnvironmentDialog newEnvironmentDialog={dialog}/>
                </>
            }
        </>
    )
}