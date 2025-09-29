import {Button, Popover, Spin} from "antd";

export default function InlineCommand({title, icon, onClick, href, className, loading}) {
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
                        href={href}
                    />
                }
                {
                    loading && <Spin size="small"/>
                }
            </Popover>
        </>
    )
}