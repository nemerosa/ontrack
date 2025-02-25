import {Tag, Tooltip} from "antd";

export function CountTag({count, color, title}) {
    return (
        <>
            {
                typeof count === 'number' &&
                <Tooltip title={title}>
                    <Tag color={color}>
                        {count}
                    </Tag>
                </Tooltip>
            }
        </>
    )
}