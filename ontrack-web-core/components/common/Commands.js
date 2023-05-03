import {CloseOutlined} from "@ant-design/icons";
import {Button, Space, Typography} from "antd";
import Link from "next/link";

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
    return <Command icon={<CloseOutlined/>} href={href}/>
}