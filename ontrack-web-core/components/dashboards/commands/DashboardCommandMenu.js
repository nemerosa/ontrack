import {Button, Dropdown, Space, Typography} from "antd";
import {FaCheck, FaCopy, FaEdit, FaLock, FaPlus, FaTrash, FaUserLock, FaUsers, FaWindowRestore} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {UserContext} from "@components/providers/UserProvider";
import {selectDashboardQuery} from "@components/dashboards/DashboardConstants";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function DashboardCommandMenu() {

    const client = useGraphQLClient()

    const {dashboards, selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)

    const user = useContext(UserContext)

    const selectDashboard = (dashboard) => {
        selectedDashboardDispatch({type: 'init', selectedDashboard: dashboard})
        client.request(selectDashboardQuery, {uuid: dashboard.uuid})
    }

    const cloneDashboard = () => {
        selectedDashboardDispatch({type: 'clone'})
    }

    const deleteDashboard = () => {
        selectedDashboardDispatch({type: 'delete'})
    }

    const editDashboard = () => {
        selectedDashboardDispatch({type: 'edit'})
    }

    const shareDashboard = () => {
        selectedDashboardDispatch({type: 'share'})
    }

    const createDashboard = () => {
        selectedDashboardDispatch({type: 'create'})
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
            let text
            if (selectedDashboard && dashboard.uuid === selectedDashboard.uuid) {
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
                onClick: () => selectDashboard(dashboard),
            })
        })

        // Separator
        menu.push({type: 'divider'})

        // Editing current
        if (selectedDashboard && selectedDashboard.authorizations?.edit) {
            menu.push({
                key: 'edit',
                icon: <FaEdit/>,
                label: "Edit current dashboard",
                onClick: editDashboard,
            })
        }

        // Sharing current
        if (selectedDashboard && selectedDashboard.authorizations?.share) {
            menu.push({
                key: 'share',
                icon: <FaUsers/>,
                label: "Share current dashboard",
                onClick: shareDashboard,
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
        if (selectedDashboard && selectedDashboard.authorizations?.delete) {
            menu.push({
                key: 'delete',
                icon: <FaTrash/>,
                label: "Delete current dashboard",
                danger: true,
                onClick: deleteDashboard,
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

        setItems(menu)
    }, [dashboards, selectedDashboard, user])

    return (
        <>
            <Dropdown menu={{items}} /*disabled={dashboard.editionMode}*/>
                <Button
                    type="text"
                >
                    <Space size={8}>
                        <FaWindowRestore/>
                        <Typography.Text /* disabled={dashboard.editionMode} */>Dashboard</Typography.Text>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}