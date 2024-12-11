import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import EnvironmentIconDialog, {
    useEnvironmentIconDialog
} from "@components/extension/environments/EnvironmentIconDialog";

export default function EnvironmentEditableIcon({environment}) {

    const user = useContext(UserContext)

    const dialog = useEnvironmentIconDialog()

    const editIcon = () => {
        dialog.start({id: environment.id})
    }

    return (
        <>
            {
                user.authorizations.environment?.edit &&
                <>
                    <EnvironmentIcon
                        environmentId={environment.id}
                        onClick={editIcon}
                        tooltipText="Edit environment icon"
                    />
                    <EnvironmentIconDialog dialog={dialog}/>
                </>
            }
            {
                !user.authorizations.environment?.edit &&
                <EnvironmentIcon environmentId={environment.id}/>
            }
        </>
    )
}