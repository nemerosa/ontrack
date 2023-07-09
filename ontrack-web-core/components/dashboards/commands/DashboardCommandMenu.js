import {Button, Dropdown, Space, Typography} from "antd";
import {Command} from "@components/common/Commands";
import {FaCheck, FaCopy, FaEdit, FaLock, FaPlus, FaTrash, FaUserLock, FaUsers, FaWindowRestore} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {UserContext} from "@components/providers/UserProvider";
import graphQLCall from "@client/graphQLCall";
import {selectDashboardQuery} from "@components/dashboards/DashboardConstants";

export default function DashboardCommandMenu() {

    const {dashboards, selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)

    const user = useContext(UserContext)

    const selectDashboard = (dashboard) => {
        return () => {
            selectedDashboardDispatch({type: 'init', selectedDashboard: dashboard})
            graphQLCall(selectDashboardQuery, {uuid: dashboard.uuid})
        }
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
                    icon = <FaUserLock/>
                    break
                case 'SHARED':
                    icon = <FaUsers/>
                    break
                case 'BUILT_IN':
                    icon = <FaLock/>
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
                label: (
                    <Command
                        icon={icon}
                        text={text}
                        action={selectDashboard(dashboard)}
                    />
                )
            })
        })

        // Separator
        menu.push({type: 'divider'})

        // Editing current
        if (selectedDashboard && selectedDashboard.authorizations?.edit) {
            menu.push({
                key: 'edit',
                label: (
                    <Command
                        icon={<FaEdit/>}
                        text="Edit current dashboard"
                        action={editDashboard}
                    />
                )
            })
        }

        // Sharing current
        if (selectedDashboard && selectedDashboard.authorizations?.share) {
            menu.push({
                key: 'share',
                label: (
                    <Command
                        icon={<FaUsers/>}
                        text="Share current dashboard"
                        action={shareDashboard}
                    />
                )
            })
        }

        // Cloning current
        menu.push({
            key: 'clone',
            label: (
                <Command
                    icon={<FaCopy/>}
                    text="Clone current dashboard"
                    action={cloneDashboard}
                />
            )
        })

        // Deleting current
        if (selectedDashboard && selectedDashboard.authorizations?.delete) {
            menu.push({
                key: 'delete',
                label: (
                    <Command
                        icon={<FaTrash/>}
                        text="Delete current dashboard"
                        action={deleteDashboard}
                    />
                )
            })
        }

        // Separator
        menu.push({type: 'divider'})

        // New dashboard
        menu.push({
            key: 'new',
            label: (
                <Command
                    icon={<FaPlus/>}
                    text="Create a new dashboard"
                    action={createDashboard}
                />
            )
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