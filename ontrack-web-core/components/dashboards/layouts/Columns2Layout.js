import {Col, Row, Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";
import {LayoutContext} from "@components/dashboards/layouts/LayoutContext";
import {useContext} from "react";

export default function Columns2Layout() {

    const widgets = useContext(LayoutContext)

    return (
        <Space direction="vertical" style={{width: '100%'}} size={16}>
            <Row
                wrap
                gutter={16}
            >
                {widgets.map((widget, index) =>
                    <Col key={index} span={12}>
                        <DashboardWidget widget={widget}/>
                    </Col>
                )}
            </Row>
        </Space>
    )
}