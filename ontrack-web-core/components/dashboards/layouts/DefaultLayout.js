import {Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";

export default function DefaultLayout({widgets}) {
    return (
        <Space direction="vertical" style={{
            width: '100%',
        }}>
            {widgets.map(widget =>
                <div style={{
                    width: '100%',
                }}>
                    <DashboardWidget widget={widget}/>
                </div>
            )}
        </Space>
    )
}