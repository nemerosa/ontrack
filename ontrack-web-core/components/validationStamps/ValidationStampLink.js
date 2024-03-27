import {Space} from "antd";
import {validationStampUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import Link from "next/link";

export default function ValidationStampLink({validationStamp, text}) {
    return <Link href={validationStampUri(validationStamp)}>
        <Space>
            <ValidationStampImage validationStamp={validationStamp}/>
            {text ? text : validationStamp.name}
        </Space>
    </Link>
}