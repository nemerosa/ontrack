import {Col, Row, Space} from "antd";
import PreviewWidget from "@components/dashboards/layouts/PreviewWidget";

export default function Main3LayoutPreview() {

    return (
        <Space direction="vertical" style={{width: '100%'}} size={16}>
            <div style={{width: '100%'}}>
                <PreviewWidget/>
            </div>
            <Row
                wrap
                gutter={16}
            >
                <Col span={8}>
                    <PreviewWidget/>
                </Col>
                <Col span={8}>
                    <PreviewWidget/>
                </Col>
                <Col span={8}>
                    <PreviewWidget/>
                </Col>
            </Row>
        </Space>
    )
}