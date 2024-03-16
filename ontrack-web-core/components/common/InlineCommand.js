import {Button, Popover} from "antd";

export default function InlineCommand({title, icon, onClick, className}) {
    return (
        <>
            <Popover
                content={title}
            >
                <Button
                    className={className}
                    type="text"
                    icon={icon}
                    onClick={onClick}
                />
            </Popover>
        </>
    )
}