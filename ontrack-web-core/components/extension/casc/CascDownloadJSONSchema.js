import {Command} from "@components/common/Commands";
import {FaFileCode} from "react-icons/fa";
import {useFullRestUri} from "@components/providers/ConnectionContextProvider";

export default function CascDownloadJSONSchema() {

    const {fullUri} = useFullRestUri('/extension/casc/download/schema/json')

    return (
        <>
            {
                fullUri &&
                <Command
                    icon={<FaFileCode/>}
                    text="JSON Schema"
                    title="Downloads the JSON schema for the Ontrack Configuration as Code"
                    href={fullUri}
                />
            }
        </>
    )
}