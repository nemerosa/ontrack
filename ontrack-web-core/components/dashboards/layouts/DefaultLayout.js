import {Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";
import {useContext} from "react";
import {LayoutContext} from "@components/dashboards/layouts/LayoutContext";

export default function DefaultLayout() {

    const widgets = useContext(LayoutContext)

    return (
        <Space direction="vertical" style={{
            width: '100%',
        }}>
            {widgets.map((widget, index) =>
                <div key={index} style={{
                    width: '100%',
                }}>
                    <DashboardWidget widget={widget}/>
                </div>
            )}
        </Space>
    )
}