import {Button, Dropdown, Space, Typography} from "antd";
import {Command} from "@components/common/Commands";
import {FaCheck, FaCopy, FaEdit, FaLock, FaUserLock, FaUsers, FaWindowRestore} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function DashboardCommandMenu() {

    const {dashboards, selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)

    const selectDashboard = (dashboard) => {
        return () => {
            selectedDashboardDispatch({type: 'init', selectedDashboard: dashboard})
        }
    }

    const cloneDashboard = (dashboard) => {
        return () => {
            selectedDashboardDispatch({type: 'clone', dashboard: dashboard})
        }
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

            // List of commands for this entry
            const commands = []
            // Cloning this entry
            commands.push(<FaCopy
                key="clone"
                onClick={cloneDashboard(dashboard)}
            />)

            // Menu entry
            menu.push({
                key: dashboard.uuid,
                label: (
                    <Command
                        icon={icon}
                        text={<Space size={8}>
                            {text}
                            {commands}
                        </Space>}
                        action={selectDashboard(dashboard)}
                    />
                )
            })
        })

        // Separator
        menu.push({type: 'divider'})

        // Cloning current
        menu.push({
            key: 'clone',
            label: (
                <Command
                    icon={<FaCopy/>}
                    text="Clone current dashboard"
                    action={cloneDashboard(selectedDashboard)}
                />
            )
        })

        setItems(menu)
    }, [dashboards, selectedDashboard])

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