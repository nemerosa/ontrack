import {Command} from "@components/common/Commands";
import {FaFileCode} from "react-icons/fa";

export default function WorkflowDownloadJSONSchema() {
    return (
        <>
            {
                <Command
                    icon={<FaFileCode/>}
                    text="JSON Schema"
                    title="Downloads the JSON schema for the Ontrack workflows"
                    href={"/api/protected/downloads/workflows/schema/json"}
                />
            }
        </>
    )
}