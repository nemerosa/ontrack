import {Tag} from "antd";

export default function GitHubLabel({label}) {
    return (
        <>
            <Tag color={`#${label.color}`}>
                {label.name}
            </Tag>
        </>
    )
}