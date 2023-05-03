import {Space} from "antd";
import {Fragment} from "react";

export default function MainPageCommands({commands}) {
    return (
        <Space
            direction="horizontal"
            wrap
            size={8}>
            {commands && commands.map((command, index) => {
                return <Fragment key={index}>{command}</Fragment>
            })}
        </Space>
    )
}