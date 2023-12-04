import {Col, Row} from "antd";

export default function Section({title, extra, padding = 0, titleWidth = 18, children}) {

    const bodyStyle = {
        padding: padding ?? 0
    }

    return (
        <div className="ot-section-container">
            <div className="ot-section-title-header">
                <Row align="middle">
                    <Col span={titleWidth} className="ot-section-title">
                        {title}
                    </Col>
                    <Col span={24 - titleWidth} className="ot-section-extra">
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