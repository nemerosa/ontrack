import {FaServer} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function EnvironmentsCommand({text = "Environments"}) {
    const user = useContext(UserContext)
    return (
        <>
            {
                user?.authorizations?.environment?.view &&
                <Command
                    icon={<FaServer/>}
                    text={text}
                    title="Access to the environments"
                    href={environmentsUri}
                />
            }
        </>
    )
}