import {Col, Row, Space} from "antd";
import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinks from "@components/builds/BuildContentLinks";

export default function BuildContent({build}) {
    return (
        <>
            <Space direction="vertical" size={16} className="ot-line">
                <Row gutter={16}>
                    <Col span={6}>
                        <BuildContentPromotions build={build}/>
                    </Col>
                    <Col span={18}>
                        <BuildContentValidations build={build}/>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={24}>
                        <BuildContentLinks build={build}/>
                    </Col>
                </Row>
            </Space>
        </>
    )
}