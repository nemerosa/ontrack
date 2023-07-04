import {Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";

export default function DefaultLayout({widgets, context, contextId, editionMode}) {
    return (
        <Space direction="vertical" style={{
            width: '100%',
        }}>
            {widgets.map((widget, index) =>
                <div key={index} style={{
                    width: '100%',
                }}>
                    <DashboardWidget
                        widget={widget}
                        context={context}
                        contextId={contextId}
                        editionMode={editionMode}
                    />
                </div>
            )}
        </Space>
    )
}