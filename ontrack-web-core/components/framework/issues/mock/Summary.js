import {Space, Tag} from "antd";

export default function IssueMockSummary({rawIssue}) {
    return (
        <>
            <Space wrap>
                Types:
                {
                    rawIssue.types.map((type, idx) => <Tag key={idx}>{type}</Tag>)
                }

                Status:
                <Tag>{rawIssue.status.name}</Tag>
            </Space>
        </>
    )
}