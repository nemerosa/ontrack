import {Button, Dropdown, Space, Typography} from "antd";
import {Command} from "@components/common/Commands";
import {FaEdit, FaWindowRestore} from "react-icons/fa";
import {useContext} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function DashboardCommandMenu() {

    const dashboard = useContext(DashboardContext)
    const dashboardDispatch = useContext(DashboardDispatchContext)

    const editDashboard = () => {
        dashboardDispatch({
            type: 'startEdition',
        })
    }

    const items = [
        {
            key: 'edit',
            label: (
                <Command
                    icon={<FaEdit/>}
                    text="Edit current"
                    action={editDashboard}
                />
            ),
        },
    ]

    return (
        <>
            <Dropdown menu={{items}} disabled={dashboard.editionMode}>
                <Button
                    type="text"
                >
                    <Space size={8}>
                        <FaWindowRestore/>
                        <Typography.Text disabled={dashboard.editionMode}>Dashboard</Typography.Text>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}