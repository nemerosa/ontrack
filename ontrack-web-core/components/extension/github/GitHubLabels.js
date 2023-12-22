import {Space} from "antd";
import GitHubLabel from "@components/extension/github/GitHubLabel";

export default function GitHubLabels({labels}) {
    return (
        <>
            <Space size={4}>
                {
                    labels.map(label => <GitHubLabel key={label.id} label={label}/>)
                }
            </Space>
        </>
    )
}