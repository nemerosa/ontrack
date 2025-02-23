import {Command} from "@components/common/Commands";
import {FaFileCode} from "react-icons/fa";
import {useFullRestUri} from "@components/providers/ConnectionContextProvider";

export default function WorkflowDownloadJSONSchema() {

    const {fullUri} = useFullRestUri('/extension/workflows/download/schema/json')

    return (
        <>
            {
                fullUri &&
                <Command
                    icon={<FaFileCode/>}
                    text="JSON Schema"
                    title="Downloads the JSON schema for the Ontrack workflows"
                    href={fullUri}
                />
            }
        </>
    )
}