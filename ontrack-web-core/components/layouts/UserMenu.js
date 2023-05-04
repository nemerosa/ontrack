import {Drawer, Menu} from "antd";
import {useContext, useEffect, useState} from "react";
import {LogoutOutlined} from "@ant-design/icons";
import {UserContext} from "@components/providers/UserProvider";
import {logout} from "@client/login";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const user = useContext(UserContext)
    const [items, setItems] = useState([])

    useEffect(() => {
        // Menu
        const menu = []
        // TODO Building the menu from the user actions
        // TODO Adding predefined "Legacy UI"
        // Adding predefined "Sign out"
        // Not working in local development mode
        menu.push({
            key: 'logout',
            label: "Sign out",
            onClick: () => {
                logout()
            },
            icon: <LogoutOutlined/>,
        })
        // Registers the menu
        setItems(menu)
    }, [user])

    const onClose = () => {
        userMenu.setOpen(false)
    }

    // const items = [
    //     {
    //         key: 'user-profile',
    //         label: "User profile",
    //         icon: <UserOutlined/>,
    //     },
    //     {
    //         type: 'divider'
    //     },
    //     {
    //         key: 'system',
    //         label: "System",
    //         icon: <SettingOutlined/>,
    //         children: [
    //             {
    //                 key: 'casc',
    //                 label: "Configuration as code",
    //             },
    //             {
    //                 key: 'global-subscriptions',
    //                 label: "Global subscriptions",
    //             },
    //         ],
    //     },
    //     {
    //         type: 'divider'
    //     },
    //     {
    //         key: 'legacy',
    //         label: "Legacy UI",
    //         icon: <Link href={legacyUri()}><PoweroffOutlined /></Link>,
    //     },
    //     {
    //         type: 'divider'
    //     },
    //     {
    //         key: 'logout',
    //         label: "Sign out",
    //         icon: <LogoutOutlined/>,
    //     },
    // ];

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
