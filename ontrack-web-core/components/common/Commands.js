import { FaTimes } from "react-icons/fa";
import {Button, Space, Typography} from "antd";
import Link from "next/link";
import {homeUri} from "@components/common/Links";

export function Command({icon, text, href, action}) {
    return <Button
        type="text"
        onClick={action}
    >
        {href && <Link href={href}>{icon}</Link>}
        {!href && <>
            <Space size={8}>
                {icon}
                <Typography.Text>{text}</Typography.Text>
            </Space>
        </>}
    </Button>
}

export function CloseCommand({href}) {
    return <Command icon={<FaTimes/>} href={href}/>
}

export function CloseToHomeCommand() {
    return <CloseCommand href={homeUri()}/>
}