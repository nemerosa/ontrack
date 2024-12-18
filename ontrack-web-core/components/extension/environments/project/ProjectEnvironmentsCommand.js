import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import {Command} from "@components/common/Commands";
import {projectEnvironmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaServer} from "react-icons/fa";

export default function ProjectEnvironmentsCommand({id}) {

    const user = useContext(UserContext)

    return (
        <>
            {
                user.authorizations.environment?.view &&
                <Command
                    href={projectEnvironmentsUri({id})}
                    icon={<FaServer/>}
                    text="Environments"
                    title="Management of environments for this project"
                />
            }
        </>
    )
}