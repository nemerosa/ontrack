import {Command} from "@components/common/Commands";
import {FaPeopleArrows} from "react-icons/fa";

export default function GroupMappingsCommand() {
    return (
        <>
            <Command
                icon={<FaPeopleArrows/>}
                text="Group mappings"
                href="/core/admin/group-mappings"
            />
        </>
    )
}