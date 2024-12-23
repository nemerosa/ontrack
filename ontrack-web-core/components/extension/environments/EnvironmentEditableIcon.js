import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import EnvironmentIconDialog, {
    useEnvironmentIconDialog
} from "@components/extension/environments/EnvironmentIconDialog";

export default function EnvironmentEditableIcon({environment, editable = true}) {

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
                    {
                        editable && <>
                            <EnvironmentIcon
                                environmentId={environment.id}
                                onClick={editIcon}
                                tooltipText="Edit environment icon"
                            />
                            <EnvironmentIconDialog dialog={dialog}/>
                        </>
                    }
                    {
                        !editable && <EnvironmentIcon
                            environmentId={environment.id}
                        />
                    }
                </>
            }
            {
                !user.authorizations.environment?.edit &&
                <EnvironmentIcon environmentId={environment.id}/>
            }
        </>
    )
}