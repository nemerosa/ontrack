import {Command} from "@components/common/Commands";
import {FaUsers} from "react-icons/fa";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";

export default function AccountGroupsCommand() {

    const user = useContext(UserContext)

    return (
        <>
            {
                user.authorizations?.account_groups?.config &&
                <Command
                    icon={<FaUsers/>}
                    text={"Groups"}
                    title="Management of account groups"
                    href={`/core/admin/account-groups`}
                />
            }
        </>
    )
}