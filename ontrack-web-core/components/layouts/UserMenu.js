import {Drawer, Menu} from "antd";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {legacyGraphiQLUri} from "@components/common/Links";
import {useRouter} from "next/router";
import {FaCode, FaDoorOpen, FaExpandArrowsAlt, FaSignOutAlt} from "react-icons/fa";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import {useLogout} from "@components/providers/ConnectionContextProvider";
import LegacyLink from "@components/common/LegacyLink";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const logout = useLogout()

    const router = useRouter()
    const {toggleExpansion} = useContext(MainLayoutContext)

    const user = useContext(UserContext)
    const [items, setItems] = useState([])

    const expandFullView = () => {
        toggleExpansion()
    }

    // const groupIcons = {
    //     Configuration: <AppstoreOutlined/>,
    //     Indicators: <CheckCircleOutlined/>,
    //     Information: <InfoCircleOutlined/>,
    //     Security: <SecurityScanOutlined/>,
    //     System: <SettingOutlined/>,
    // }

    const createMenuItem = (action) => {
        return {
            key: action.id,
            label: action.name,
            onClick: () => {
                if (action.type === 'LINK' && action.uri) {
                    // TODO Legacy vs. Next UI
                    // noinspection JSIgnoredPromiseFromCall
                    let uri = action.uri
                    if (!uri.startsWith('/')) {
                        uri = '/' + uri
                    }
                    router.push(uri)
                } else {
                    console.log(`Unsupported action type ${action.type} for action ${action.id} (${action.name}).`)
                }
            }
        }
    }

    useEffect(() => {
        const menu = []
        // TODO Loading the user menu from the server?
        // if (user?.actions) {
        //     // Menu
        //     const topLevelActions = []
        //     const groupIndex = {}
        //     // Building the menu from the user actions
        //     user.actions.forEach(action => {
        //         const groupName = action.group
        //         if (groupName) {
        //             let group = groupIndex[groupName]
        //             if (!group) {
        //                 group = {
        //                     key: groupName,
        //                     label: groupName,
        //                     icon: groupIcons[groupName],
        //                     children: [],
        //                 }
        //                 groupIndex[groupName] = group
        //             }
        //             group.children.push(createMenuItem(action))
        //         } else {
        //             topLevelActions.push(createMenuItem(action))
        //         }
        //     })
        //     // Top level actions at the start
        //     // TODO menu.push(...topLevelActions)
        //     // Sorting the groups
        //     const groups = [];
        //     for (const [_, group] of Object.entries(groupIndex)) {
        //         groups.push(group);
        //     }
        //     groups.sort((a, b) => {
        //         const na = a.key;
        //         const nb = b.key;
        //         if (na < nb) {
        //             return -1;
        //         } else if (na > nb) {
        //             return 1;
        //         } else {
        //             return 0;
        //         }
        //     });
        //     // Sorting items in groups
        //     groups.forEach(group => {
        //         group.children.sort((a, b) => {
        //             const na = a.label;
        //             const nb = b.label;
        //             if (na < nb) {
        //                 return -1;
        //             } else if (na > nb) {
        //                 return 1;
        //             } else {
        //                 return 0;
        //             }
        //         })
        //     })
        //     // Groups in the menu
        //     // TODO menu.push(...groups)
        //     // TODO Separator
        //     // menu.push({
        //     //     type: 'divider',
        //     // })
        // }
        // GraphiQL
        menu.push({
            key: 'graphiql',
            label: <LegacyLink href={legacyGraphiQLUri()}>GraphiQL</LegacyLink>,
            title: "GraphQL IDE",
            icon: <FaCode/>,
        })
        // Separator
        menu.push({
            type: 'divider',
        })
        // Adding predefined "Legacy UI"
        menu.push({
            key: 'legacy',
            label: <LegacyLink href="/">Legacy UI</LegacyLink>,
            icon: <FaDoorOpen/>,
        })
        // Full view toggle
        menu.push({
            key: 'expand',
            label: "Full view",
            icon: <FaExpandArrowsAlt/>,
            onClick: expandFullView,
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
            icon: <FaSignOutAlt/>,
            onClick: () => {
                if (logout) logout.call()
            },
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
