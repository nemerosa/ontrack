import {FaServer} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function EnvironmentsCommand() {
    const user = useContext(UserContext)
    return (
        <>
            {
                user.authorizations.environment?.view &&
                <Command
                    icon={<FaServer/>}
                    text="Environments"
                    title="Access to the environments"
                    href={environmentsUri}
                />
            }
        </>
    )
}