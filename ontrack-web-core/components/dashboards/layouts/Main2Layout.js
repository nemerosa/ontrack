import {Col, Row, Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";
import {LayoutContext} from "@components/dashboards/layouts/LayoutContext";
import {useContext} from "react";

export default function Main2Layout() {

    const widgets = useContext(LayoutContext)

    return (
        <Space direction="vertical" style={{width: '100%'}} size={16}>
            {widgets.length &&
                <div style={{width: '100%'}}>
                    <DashboardWidget widget={widgets[0]}/>
                </div>
            }
            {
                widgets.length > 1 &&
                <Row
                    wrap
                    gutter={16}
                >
                    {widgets.slice(1).map((widget, index) =>
                        <Col key={index} span={12}>
                            <DashboardWidget widget={widget}/>
                        </Col>
                    )}
                </Row>
            }
        </Space>
    )
}