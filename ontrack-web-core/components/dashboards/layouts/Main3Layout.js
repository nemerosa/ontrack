import {Col, Row, Space} from "antd";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";

export default function Main2Layout({widgets, context, contextId, editionMode}) {
    return (
        <Space direction="vertical" style={{width: '100%'}} size={16}>
            {widgets.length &&
                <div style={{width: '100%'}}>
                    <DashboardWidget
                        widget={widgets[0]}
                        context={context}
                        contextId={contextId}
                        editionMode={editionMode}
                    />
                </div>
            }
            {
                widgets.length > 1 &&
                <Row
                    wrap
                    gutter={16}
                >
                    {widgets.slice(1).map((widget, index) =>
                        <Col key={index} span={8}>
                            <DashboardWidget
                                widget={widget}
                                context={context}
                                contextId={contextId}
                            />
                        </Col>
                    )}
                </Row>
            }
        </Space>
    )
}