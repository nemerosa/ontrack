import {Drawer, Menu} from "antd";
import {useContext, useEffect, useState} from "react";
import {LogoutOutlined, PoweroffOutlined} from "@ant-design/icons";
import {UserContext} from "@components/providers/UserProvider";
import {logout} from "@client/login";
import {legacyUri} from "@components/common/Links";
import {useRouter} from "next/router";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const router = useRouter()

    const user = useContext(UserContext)
    const [items, setItems] = useState([])

    useEffect(() => {
        if (user?.actions) {
            // Menu
            const menu = []
            const groups = {}
            // Building the menu from the user actions
            user.actions.forEach(action => {
                const groupName = action.group
                if (groupName) {

                } else {
                    menu.push({
                        key: action.id,
                        label: action.name,
                        onClick: () => {
                            if (action.type === 'LINK' && action.uri) {
                                // TODO Legacy vs. Next UI
                                // noinspection JSIgnoredPromiseFromCall
                                router.push(action.uri)
                            } else {
                                console.log(`Unsupported action type ${action.type} for action ${action.id} (${action.name}).`)
                            }
                        }
                    })
                }
            })
            // TODO Sorting root menu items
            // TODO Sorting items in groups
            // Separator
            menu.push({
                type: 'divider',
            })
            // Adding predefined "Legacy UI"
            menu.push({
                key: 'legacy',
                label: "Legacy UI",
                icon: <PoweroffOutlined/>,
                onClick: () => {
                    location.href = legacyUri()
                }
            })
            // Separator
            menu.push({
                type: 'divider',
            })
            // Adding predefined "Sign out"
            // Not working in local development mode
            menu.push({
                key: 'logout',
                label: "Sign out",
                icon: <LogoutOutlined/>,
                onClick: () => {
                    logout()
                },
            })
            // Registers the menu
            setItems(menu)
        }
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
