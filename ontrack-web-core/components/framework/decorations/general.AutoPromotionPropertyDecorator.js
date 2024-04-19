import {Tooltip, Typography} from "antd";
import {FaBolt} from "react-icons/fa";

export default function AutoPromotionPropertyDecorator({decoration}) {
    return (
        <Tooltip title="Auto promotion enabled">
            <Typography.Text>
                <FaBolt color="#FC0"/>
            </Typography.Text>
        </Tooltip>
    )
}