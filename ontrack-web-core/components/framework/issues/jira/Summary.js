import {Space, Tag} from "antd";
import Image from "next/image";

export default function IssueJiraSummary({rawIssue}) {
    return (
        <>
            <Space wrap>

                Status:
                <Image src={rawIssue.status.icon_url} alt={rawIssue.status.name} width={16} height={16}/>
                {rawIssue.status.name}

                {
                    rawIssue.affectedVersions && <>
                        Affected versions:
                        {
                            rawIssue.affectedVersions.map(version => <>
                                <Tag>{version / name}</Tag>
                            </>)
                        }
                    </>
                }

                {
                    rawIssue.fixVersions && <>
                        Fix versions:
                        {
                            rawIssue.fixVersions.map(version => <>
                                <Tag>{version / name}</Tag>
                            </>)
                        }
                    </>
                }

            </Space>
        </>
    )
}