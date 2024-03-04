import {Tag} from "antd";

export default function FractionValidationDataType({numerator, denominator}) {
    return <Tag>
        {numerator}
        /
        {denominator}
    </Tag>
}