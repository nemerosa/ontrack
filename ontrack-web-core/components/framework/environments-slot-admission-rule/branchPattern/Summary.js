import {Space, Tag, Typography} from "antd";

export default function BranchPatternAdmissionRuleSummary({lastBranchOnly, includes, excludes}) {
    return (
        <>
            <Space>
                {
                    lastBranchOnly &&
                    <Tag>Last branch only</Tag>
                }
                {
                    includes && includes.length > 0 && <>
                        <Typography.Text>Includes</Typography.Text>
                        {
                            includes.map((regex, index) => (
                                <Tag key={index}>{regex}</Tag>
                            ))
                        }
                    </>
                }
                {
                    excludes && excludes.length > 0 && <>
                        <Typography.Text>Excludes</Typography.Text>
                        {
                            excludes.map((regex, index) => (
                                <Tag key={index}>{regex}</Tag>
                            ))
                        }
                    </>
                }
            </Space>
        </>
    )
}