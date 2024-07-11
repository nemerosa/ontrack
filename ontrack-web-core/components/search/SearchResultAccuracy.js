import {Typography} from "antd";

export default function SearchResultAccuracy({accuracy}) {
    return (
        <>
            <Typography.Text type="secondary" title="Accuracy of the match">{accuracy.toFixed(1)}</Typography.Text>
        </>
    )
}