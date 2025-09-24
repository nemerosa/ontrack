import PageSection from "@components/common/PageSection";
import {Divider, Space, Typography} from "antd";
import SafeHTMLComponent from "@components/common/SafeHTMLComponent";
import TimestampText from "@components/common/TimestampText";

export default function SCMCommitInfo({scmCommitInfo}) {
    return (
        <>
            <PageSection
                title={
                    <Space size="small">
                        Commit
                        <Typography.Link
                            code
                            href={scmCommitInfo.scmDecoratedCommit.commit.link}
                        >{scmCommitInfo.scmDecoratedCommit.commit.id}</Typography.Link>
                    </Space>
                }
                padding={true}
            >
                <Space direction="vertical">
                    <Typography.Paragraph>
                        <SafeHTMLComponent htmlContent={scmCommitInfo.scmDecoratedCommit.annotatedMessage}/>
                    </Typography.Paragraph>
                    <Space size="small">
                        <Typography.Text
                            type="secondary">{scmCommitInfo.scmDecoratedCommit.commit.author}</Typography.Text>
                        <Divider type="vertical"/>
                        <Typography.Text
                            type="secondary">
                            <TimestampText value={scmCommitInfo.scmDecoratedCommit.commit.timestamp}/>
                        </Typography.Text>
                    </Space>
                </Space>
            </PageSection>
        </>
    )
}