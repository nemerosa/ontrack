import {Button, Tooltip} from "antd";

export default function WidgetCommand({title, icon, onAction}) {
    return (
        <>
            <Tooltip title={title}>
                <div>
                    <Button icon={icon} onClick={onAction}/>
                </div>
            </Tooltip>
        </>
    )
}