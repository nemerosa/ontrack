import {Button, Dropdown, Space, Typography} from "antd";
import {Command} from "@components/common/Commands";
import {FaCheck, FaEdit, FaLock, FaUserLock, FaUsers, FaWindowRestore} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function DashboardCommandMenu() {

    const {dashboards, selectedDashboard} = useContext(DashboardContext)
    // const dashboardDispatch = useContext(DashboardDispatchContext)
    //
    // const editDashboard = () => {
    //     dashboardDispatch({
    //         type: 'startEdition',
    //     })
    // }

    const selectDashboard = (dashboard) => {
        return () => {
            // TODO
        }
    }

    const [items, setItems] = useState([])

    useEffect(() => {
        const menu = []
        dashboards.forEach(dashboard => {
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
            let text
            if (selectedDashboard && dashboard.uuid === selectedDashboard.uuid) {
                text = <Space size={8}>
                    <FaCheck/>
                    <Typography.Text strong>{dashboard.name}</Typography.Text>
                </Space>
            } else {
                text = <Typography.Text>{dashboard.name}</Typography.Text>
            }
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