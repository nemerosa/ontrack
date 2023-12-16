import {Button, Tooltip} from "antd";

export default function GridCellCommand({condition = true, disabled = false, title, icon, onAction, className, children}) {
    return (
        <>
            {
                condition &&
                <Tooltip title={title}>
                    <div>
                        <Button disabled={disabled} className={className} icon={icon} onClick={onAction}>{children}</Button>
                    </div>
                </Tooltip>
            }
        </>
    )
}