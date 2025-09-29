import {Divider, Space, Typography} from "antd";
import PageSection from "@components/common/PageSection";
import SCMCommitInfo from "@components/extension/scm/SCMCommitInfo";
import {Dynamic} from "@components/common/Dynamic";

export default function SCMIssueInfo({scmIssueInfo}) {
    return (
        <>
            <Space direction="vertical" className="ot-line">
                <PageSection
                    title={
                        <Space size="small">
                            Issue
                            <Typography.Link
                                code
                                href={scmIssueInfo.issue.url}
                            >{scmIssueInfo.issue.displayKey}</Typography.Link>
                            <Divider type="vertical"/>
                            <Typography.Text type="secondary">{scmIssueInfo.issue.summary}</Typography.Text>
                        </Space>
                    }
                    padding={true}
                >
                    <Dynamic
                        path={`framework/issues/${scmIssueInfo.issueServiceConfigurationRepresentation.serviceId}/Summary`}
                        props={{...scmIssueInfo.issue}}
                    />
                </PageSection>
                {
                    scmIssueInfo.scmCommitInfo &&
                    <SCMCommitInfo scmCommitInfo={scmIssueInfo.scmCommitInfo}/>
                }
            </Space>
        </>
    )
}