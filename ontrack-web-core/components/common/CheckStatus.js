import {Space, Typography} from "antd";
import CheckIcon from "@components/common/CheckIcon";

export default function CheckStatus({value, text, noText}) {
    return (
        <>
            <Space>
                <CheckIcon value={value}/>
                {
                    value && <Typography.Text>{text}</Typography.Text>
                }
                {
                    !value && <Typography.Text>{noText || text}</Typography.Text>
                }
            </Space>
        </>
    )
}