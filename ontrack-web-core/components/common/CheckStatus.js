import {Space, Tooltip, Typography} from "antd";
import CheckIcon from "@components/common/CheckIcon";

export default function CheckStatus({value, text, noText, tooltip}) {
    const actualTooltip = tooltip &&
        (typeof tooltip === 'function' ?
                tooltip(value) :
                tooltip
        )
    return (
        <>
            <Tooltip title={actualTooltip}>
                <Space>
                    <CheckIcon value={value}/>
                    {
                        value && <Typography.Text>{text}</Typography.Text>
                    }
                    {
                        !value && <Typography.Text>{noText || text}</Typography.Text>
                    }
                </Space>
            </Tooltip>
        </>
    )
}