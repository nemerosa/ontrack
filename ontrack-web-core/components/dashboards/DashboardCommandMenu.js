import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {UserContext} from "@components/providers/UserProvider";
import {FaCheck, FaCopy, FaEdit, FaLock, FaPlus, FaTrash, FaUserLock, FaUsers, FaWindowRestore} from "react-icons/fa";
import {Button, Dropdown, Space, Typography} from "antd";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";
import SaveDashboardDialog, {useSaveDashboardDialog} from "@components/dashboards/SaveDashboardDialog";
import {gqlDashboardFragment} from "@components/dashboards/DashboardConstants";

export default function DashboardCommandMenu() {

    const client = useGraphQLClient()
    const user = useContext(UserContext)

    const [dashboards, setDashboards] = useState([])
    const context = useContext(DashboardContext)

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query UserDashboards {
                        userDashboards {
                            ...DashboardData
                        }
                    }
                    
                    ${gqlDashboardFragment}
                `
            ).then(data => {
                setDashboards(data.userDashboards)
            })
        }
    }, [client, context?.dashboard])

    const saveDashboardDialog = useSaveDashboardDialog({
        onSuccess: (_, {__, edition}) => {
            context.refresh()
            if (edition) {
                context.startEdition()
            }
        }
    })

    const editDashboard = () => {
        context.startEdition()
    }

    const cloneDashboard = () => {
        const selectedDashboard = context?.dashboard
        if (selectedDashboard) {
            const copy = {
                ...selectedDashboard,
                uuid: '',
                name: `Copy of ${selectedDashboard.name}`,
                userScope: 'PRIVATE',
                widgets: selectedDashboard.widgets.map(widget => ({
                    ...widget,
                    uuid: ''
                }))
            }
            // Opening the dialog
            saveDashboardDialog.start({copy})
        }
    }

    const createDashboard = () => {
        const copy = {
            uuid: '',
            name: `New dashboard`,
            userScope: 'PRIVATE',
            widgets: [],
        }
        // Opening the dialog
        saveDashboardDialog.start({copy, edition: true})
    }

    const [items, setItems] = useState([])

    useEffect(() => {
        const menu = []

        // All dashboards
        dashboards.forEach(dashboard => {

            // Type of dashboards
            let icon = undefined
            switch (dashboard.userScope) {
                case 'PRIVATE':
                    icon = <FaUserLock title="This dashboard is visible only to you."/>
                    break
                case 'SHARED':
                    icon = <FaUsers title="This dashboard is shared for all users."/>
                    break
                case 'BUILT_IN':
                    icon = <FaLock title="This dashboard built-in and cannot be edited."/>
                    break
            }

            // Selection indicator
            let text = <Typography.Text>{dashboard.name}</Typography.Text>
            if (context?.dashboard && context?.dashboard?.uuid === dashboard.uuid) {
                text = <Space size={8}>
                    <FaCheck/>
                    <Typography.Text strong>{dashboard.name}</Typography.Text>
                </Space>
            } else {
                text = <Typography.Text>{dashboard.name}</Typography.Text>
            }

            // Menu entry
            menu.push({
                key: dashboard.uuid,
                icon: icon,
                label: text,
                onClick: () => context.selectDashboard(dashboard),
            })
        })

        // Separator
        menu.push({type: 'divider'})

        // Editing current
        if (context?.dashboard && context.dashboard.authorizations.edit) {
            menu.push({
                key: 'edit',
                icon: <FaEdit/>,
                label: "Edit current dashboard",
                onClick: editDashboard,
            })
        }

        // Sharing current
        if (context?.dashboard && context.dashboard.userScope === 'PRIVATE' && context.dashboard.authorizations?.share) {
            menu.push({
                key: 'share',
                icon: <FaUsers/>,
                label: "Share current dashboard",
                onClick: context.shareDashboard,
            })
        }

        // Cloning current
        menu.push({
            key: 'clone',
            icon: <FaCopy/>,
            label: "Clone current dashboard",
            onClick: cloneDashboard,
        })

        // Deleting current
        if (context?.dashboard && context.dashboard.authorizations?.delete) {
            menu.push({
                key: 'delete',
                icon: <FaTrash/>,
                label: "Delete current dashboard",
                danger: true,
                onClick: context.deleteDashboard,
            })
        }

        // Separator
        menu.push({type: 'divider'})

        // New dashboard
        menu.push({
            key: 'new',
            icon: <FaPlus/>,
            label: "Create a new dashboard",
            onClick: createDashboard,
        })

        // OK
        setItems(menu)

    }, [dashboards, context?.dashboard, user]);

    return (
        <>
            <Dropdown menu={{items}}>
                <Button
                    type="text"
                >
                    <Space size={8}>
                        <FaWindowRestore/>
                        <Typography.Text>Dashboard</Typography.Text>
                    </Space>
                </Button>
            </Dropdown>
            <SaveDashboardDialog saveDashboardDialog={saveDashboardDialog}/>
        </>
    )
}