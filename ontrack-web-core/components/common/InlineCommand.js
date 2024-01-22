import {Button, Popover} from "antd";

export default function InlineCommand({title, icon, onClick}) {
    return (
        <>
            <Popover
                content={title}
            >
                <Button
                    type="text"
                    icon={icon}
                    onClick={onClick}
                />
            </Popover>
        </>
    )
}