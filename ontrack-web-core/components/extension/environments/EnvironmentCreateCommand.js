import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";

export default function EnvironmentCreateCommand() {
    return (
        <>
            <Command
                icon={<FaPlus/>}
                text="New environment"
                title="Create a new environment"
            />
        </>
    )
}