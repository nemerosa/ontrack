import {FaEdit, FaTimes} from "react-icons/fa";
import {Button, Space, Typography} from "antd";
import Link from "next/link";
import {homeUri} from "@components/common/Links";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";

export function Command({icon, text, href, action}) {
    return <Button
        type="text"
        onClick={action}
    >
        {href && <Link href={href}>{icon} {text}</Link>}
        {!href && <>
            <Space size={8}>
                {icon}
                <Typography.Text>{text}</Typography.Text>
            </Space>
        </>}
    </Button>
}

export function CloseCommand({href}) {
    return <Command icon={<FaTimes/>} href={href} text="Close"/>
}

export function CloseToHomeCommand() {
    return <CloseCommand href={homeUri()}/>
}

export function DashboardEditCommand() {
    const user = useContext(UserContext)
    return (
        <>
            {
                user.authorizations?.dashboard?.edit ? <Command
                    icon={<FaEdit/>}
                    text="Edit dashboard"
                /> : undefined
            }
        </>
    )
}