import {Tooltip, Typography} from "antd";
import MessageTypeIcon from "@components/common/MessageTypeIcon";

export default function MessageDecorationExtension({decoration}) {
    return (
        <>
            <Tooltip title={decoration.data.text}>
                <Typography.Text>
                    <MessageTypeIcon type={decoration.data.type}/>
                </Typography.Text>
            </Tooltip>
        </>
    )
}