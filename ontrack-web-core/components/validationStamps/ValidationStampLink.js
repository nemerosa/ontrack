import LegacyLink from "@components/common/LegacyLink";
import {Space} from "antd";
import {validationStampUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export default function ValidationStampLink({validationStamp, text}) {
    return <LegacyLink href={validationStampUri(validationStamp)}>
        <Space>
            <ValidationStampImage validationStamp={validationStamp}/>
            {text ? text : validationStamp.name}
        </Space>
    </LegacyLink>
}