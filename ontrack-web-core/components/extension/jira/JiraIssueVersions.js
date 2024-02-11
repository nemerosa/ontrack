import {Space, Tag} from "antd";

export default function JiraIssueVersions({versions = []}) {
    return (
        <>
            <Space size={2}>
                {
                    versions.map(({name, released}, index) =>
                        <Tag
                            key={`version-${index}`}
                            color={released ? 'success' : 'warning'}
                        >
                            {name}
                        </Tag>
                    )
                }
            </Space>
        </>
    )
}