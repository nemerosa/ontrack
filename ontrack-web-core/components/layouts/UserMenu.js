import {Drawer, Menu, Space, Typography} from "antd";
import {useState} from "react";
import {LogoutOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";

const {Text} = Typography;

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
