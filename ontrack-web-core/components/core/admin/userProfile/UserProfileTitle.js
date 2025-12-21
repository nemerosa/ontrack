import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import {Typography} from "antd";

const {Text} = Typography;

export default function UserProfileTitle() {
    const {email} = useContext(UserContext)

    return <Typography>
        User profile (<Text code>{email}</Text>)
    </Typography>
}