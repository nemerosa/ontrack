import {FaDoorOpen, FaTimes} from "react-icons/fa";
import {Button, Space, Typography} from "antd";
import Link from "next/link";
import {homeUri} from "@components/common/Links";
import LegacyLink from "@components/common/LegacyLink";

export function Command({icon, text, href, action, title, legacy = false, disabled = false}) {
    return <Button
        type="text"
        onClick={action}
        title={title}
        disabled={disabled}
    >
        {href && !legacy && <Link href={href}>{icon} {text}</Link>}
        {href && legacy && <LegacyLink href={href}>{icon} {text}</LegacyLink>}
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

export function LegacyLinkCommand({href, text, title}) {
    return <Command icon={<FaDoorOpen/>}
                    href={href}
                    text={text}
                    title={title}
                    legacy={true}
    />
}