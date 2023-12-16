import {Typography} from "antd";
import TimestampText from "@components/common/TimestampText";

export default function Timestamp({
                                      value,
                                      type = "secondary",
                                      italic = true,
                                      fontSize = '75%',
                                      prefix = '',
                                      suffix = '',
                                      format = undefined,
                                  }) {
    return (
        <>
            <Typography.Text type={type} italic={italic} style={{
                fontSize: fontSize,
            }}>
                <TimestampText value={value} prefix={prefix} suffix={suffix} format={format}/>
            </Typography.Text>
        </>
    )
}