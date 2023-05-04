import {Drawer, Menu} from "antd";
import {useState} from "react";
import {LogoutOutlined, PoweroffOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import Link from "next/link";
import {legacyUri} from "@components/common/Links";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const onClose = () => {
        userMenu.setOpen(false)
    }

    const items = [
        {
            key: 'user-profile',
            label: "User profile",
            icon: <UserOutlined/>,
        },
        {
            type: 'divider'
        },
        {
            key: 'system',
            label: "System",
            icon: <SettingOutlined/>,
            children: [
                {
                    key: 'casc',
                    label: "Configuration as code",
                },
                {
                    key: 'global-subscriptions',
                    label: "Global subscriptions",
                },
            ],
        },
        {
            type: 'divider'
        },
        {
            key: 'legacy',
            label: "Legacy UI",
            icon: <Link href={legacyUri()}><PoweroffOutlined /></Link>,
        },
        {
            type: 'divider'
        },
        {
            key: 'logout',
            label: "Sign out",
            icon: <LogoutOutlined/>,
        },
    ];

    const onClick = (e) => {
        console.log("User menu: ", e.key)
        onClose()
    };

    return (
        <>
            <Drawer placement="right"
                    open={userMenu.open}
                    closable={false}
                    onClose={onClose}
            >
                <Menu mode="inline"
                      selectable={false}
                      items={items}
                      onClick={onClick}
                >
                </Menu>
            </Drawer>
        </>
    )
}
