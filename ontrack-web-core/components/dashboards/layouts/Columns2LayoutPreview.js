import {Col, Row} from "antd";
import PreviewWidget from "@components/dashboards/layouts/PreviewWidget";

export default function Columns2LayoutPreview() {

    return (
        <Row
            wrap
            gutter={16}
        >
            <Col span={12}>
                <PreviewWidget/>
            </Col>
            <Col span={12}>
                <PreviewWidget/>
            </Col>
        </Row>
    )
}