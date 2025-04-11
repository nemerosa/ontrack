import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {Command} from "@components/common/Commands";
import {FaUser} from "react-icons/fa";

export default function UserAccountCommand() {
    const user = useContext(UserContext)
    if (user?.profile?.auth?.account?.url) {
        return <Command
            text="Account management"
            title="Go to the management interface of your account. Can be used to change your password."
            icon={<FaUser/>}
            href={user.profile.auth.account.url}
            target="_blank"
        />
    } else {
        return null
    }
}