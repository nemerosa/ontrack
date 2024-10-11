import {FaServer} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLink";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";

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