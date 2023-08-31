import {Col, Row} from "antd";
import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import BuildContentValidations from "@components/builds/BuildContentValidations";

export default function BuildContent({build}) {
    return (
        <>
            <Row gutter={16}>
                <Col span={6}>
                    <BuildContentPromotions build={build}/>
                </Col>
                <Col span={18}>
                    <BuildContentValidations build={build}/>
                </Col>
            </Row>
            {/*  TODO Used by / Using  */}
        </>
    )
}