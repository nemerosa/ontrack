import {Col, Row} from "antd";

export default function Section({title, extra, padding = 0, children}) {

    const bodyStyle = {
        padding: padding ?? 0
    }

    return (
        <div className="ot-section-container">
            <div className="ot-section-title-header">
                <Row align="middle">
                    <Col span={18} className="ot-section-title">
                        {title}
                    </Col>
                    <Col span={6} className="ot-section-extra">
                        {extra}
                    </Col>
                </Row>
            </div>
            <div
                className="ot-section-body"
                style={bodyStyle}
            >
                {children}
            </div>
        </div>
    )
}