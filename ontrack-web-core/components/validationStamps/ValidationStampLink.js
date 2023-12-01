import LegacyLink from "@components/common/LegacyLink";
import {Space} from "antd";
import {legacyValidationStampUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export default function ValidationStampLink({validationStamp, text}) {
    return <LegacyLink href={legacyValidationStampUri(validationStamp)}>
        <Space>
            <ValidationStampImage validationStamp={validationStamp}/>
            {text ? text : validationStamp.name}
        </Space>
    </LegacyLink>
}