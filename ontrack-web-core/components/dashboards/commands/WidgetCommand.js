import {Button, Tooltip} from "antd";

export default function WidgetCommand({condition, title, icon, onAction}) {
    return (
        <>
            {
                condition &&
                <Tooltip title={title}>
                    <div>
                        <Button icon={icon} onClick={onAction}/>
                    </div>
                </Tooltip>
            }
        </>
    )
}