import {Drawer, Menu, Typography} from "antd";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {legacyGraphiQLUri} from "@components/common/Links";
import {
    FaBars,
    FaCertificate,
    FaCode,
    FaCog,
    FaCogs,
    FaDoorOpen,
    FaExpandArrowsAlt,
    FaGithub,
    FaInfo,
    FaJenkins,
    FaJira,
    FaList,
    FaMagic,
    FaMailBulk,
    FaMedal,
    FaPaperPlane,
    FaProjectDiagram,
    FaServer,
    FaSignOutAlt,
    FaStamp,
    FaTag,
    FaUser,
    FaWrench
} from "react-icons/fa";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import LegacyLink from "@components/common/LegacyLink";
import UserMenuItemLink from "@components/layouts/UserMenuItemLink";
import {useRefData} from "@components/providers/RefDataProvider";
import {signOut} from "next-auth/react";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}


export const groupIcons = {
    configurations: <FaList/>,
    system: <FaCog/>,
    user: <FaUser/>,
    information: <FaInfo/>,
}

export default function UserMenu({userMenu}) {

    const logout = () => signOut()

    const {toggleExpansion} = useContext(MainLayoutContext)
    const {version} = useRefData()

    const user = useContext(UserContext)
    const [items, setItems] = useState([])

    const expandFullView = () => {
        toggleExpansion()
    }

    const itemIcons = {
        'core/config/predefined-promotion-levels': <FaMedal/>,
        'core/config/predefined-validation-stamps': <FaStamp/>,
        'core/admin/jobs': <FaCogs/>,
        'core/admin/settings': <FaWrench/>,
        'extension/jenkins/configurations': <FaJenkins/>,
        'extension/github/configurations': <FaGithub/>,
        'extension/jira/configurations': <FaJira/>,
        'extension/auto-versioning/audit/global': <FaMagic/>,
        'extension/notifications/subscriptions/global': <FaPaperPlane/>,
        'extension/notifications/recordings': <FaMailBulk/>,
        'extension/workflows/audit': <FaProjectDiagram/>,
        'extension/environments/environments': <FaServer/>,
        'extension/license/info': <FaCertificate/>,
        'extension/casc/casc': <FaCode/>,
        'extension/queue/records': <FaBars/>,
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
                    icon: itemIcons[`${item.extension}/${item.id}`],
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
            onClick: logout,
        })
        // Version
        menu.push({
            type: 'divider',
        })
        menu.push({
            key: 'version',
            label: <Typography.Text type="secondary">Version: {version}</Typography.Text>,
            icon: <FaTag/>,
            disabled: true,
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
