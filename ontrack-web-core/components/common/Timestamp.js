import {Typography} from "antd";
import dayjs from "dayjs";

export default function Timestamp({
                                      value,
                                      type = "secondary",
                                      italic = true,
                                      fontSize = '75%',
                                      prefix = '',
                                  }) {
    return (
        <>
            <Typography.Text type={type} italic={italic} style={{
                fontSize: fontSize,
            }}>
                {prefix && `${prefix} `}
                {dayjs(value).format("YYYY MMM DD, HH:mm")}
            </Typography.Text>
        </>
    )
}