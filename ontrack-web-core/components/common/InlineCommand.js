import {Button, Popover, Spin} from "antd";

export default function InlineCommand({title, icon, onClick, className, loading}) {
    return (
        <>
            <Popover
                content={title}
            >
                {
                    !loading &&
                    <Button
                        className={className}
                        type="text"
                        icon={icon}
                        onClick={onClick}
                    />
                }
                {
                    loading && <Spin size="small"/>
                }
            </Popover>
        </>
    )
}