import {Tag} from "antd";

export default function GitHubState({state, text}) {
    return (
        <Tag color={
            state === 'open' ? 'success' : 'warning'
        }>
            {text ?? state}
        </Tag>
    )
}
