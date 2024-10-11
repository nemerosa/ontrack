import {FaServer} from "react-icons/fa";
import {Command} from "@components/common/Commands";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLink";

export default function EnvironmentsCommand() {
    return (
        <>
            <Command
                icon={<FaServer/>}
                text="Environments"
                title="Access to the environments"
                href={environmentsUri}
            />
        </>
    )
}