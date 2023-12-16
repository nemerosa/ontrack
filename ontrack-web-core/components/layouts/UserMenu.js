import {Drawer, Menu} from "antd";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {legacyGraphiQLUri} from "@components/common/Links";
import {FaCode, FaCog, FaDoorOpen, FaExpandArrowsAlt, FaSignOutAlt, FaUser} from "react-icons/fa";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import {useLogout} from "@components/providers/ConnectionContextProvider";
import LegacyLink from "@components/common/LegacyLink";
import Link from "next/link";
import UserMenuItemLink from "@components/layouts/UserMenuItemLink";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const logout = useLogout()

    const {toggleExpansion} = useContext(MainLayoutContext)

    const user = useContext(UserContext)
    const [items, setItems] = useState([])

    const expandFullView = () => {
        toggleExpansion()
    }

    const groupIcons = {
        system: <FaCog/>,
        user: <FaUser/>,
    }

    useEffect(() => {
        const menu = []
        // All groups
        user.userMenuGroups.forEach(group => {
            menu.push({
                key: group.id,
                label: group.name,
                icon: groupIcons[group.id],
                children: group.items.map(item => ({
                    key: `${item.extension}-${item.id}`,
                    label: <UserMenuItemLink item={item}/>,
                }))
            })
            menu.push({
                type: 'divider',
            })
        })
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

    const onClick = () => {
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
