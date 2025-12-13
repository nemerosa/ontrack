import {Command} from "@components/common/Commands";
import {FaFileCode} from "react-icons/fa";

export default function CascDownloadJSONSchema() {
    return (
        <>
            {
                <Command
                    icon={<FaFileCode/>}
                    text="JSON Schema"
                    title="Downloads the JSON schema for the Ontrack Configuration as Code"
                    href={"/api/protected/downloads/casc/schema/json"}
                />
            }
        </>
    )
}