import {Command} from "@components/common/Commands";
import {FaKey} from "react-icons/fa";

export default function GlobalPermissionsCommand() {
    return (
        <>
            <Command
                icon={<FaKey/>}
                title="Management of global permissions"
                text="Global permissions"
                href="/core/admin/global-permissions"
            />
        </>
    )
}