import {CloseOutlined} from "@ant-design/icons";
import {Button} from "antd";
import Link from "next/link";

export function Command({icon, href}) {
    return <Button
        type="text"
    >
        <Link href={href}>{icon}</Link>
    </Button>
}

export function CloseCommand({href}) {
    return <Command icon={<CloseOutlined/>} href={href}/>
}