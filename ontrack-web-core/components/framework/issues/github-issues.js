import {Table, Typography} from "antd";
import Link from "next/link";
import GitHubIssueState from "@components/extension/github/GitHubIssueState";
import GitHubMilestone from "@components/extension/github/GitHubMilestone";
import GitHubUser from "@components/extension/github/GitHubUser";
import TimestampText from "@components/common/TimestampText";
import GitHubLabels from "@components/extension/github/GitHubLabels";

const {Column} = Table

export default function GithubIssues({issues}) {
    return (
        <>
            <Table
                dataSource={issues}
                size="small"
            >
                <Column
                    key="id"
                    title="ID"
                    render={(_, {issue}) => (
                        <Link href={issue.url}>
                            <Typography.Text code>
                                {issue.displayKey}
                            </Typography.Text>
                        </Link>
                    )}
                />
                <Column
                    key="state"
                    title="State"
                    render={(_, {issue}) => (
                        <GitHubIssueState state={issue.state}/>
                    )}
                />
                <Column
                    key="title"
                    title="Title"
                    render={(_, {issue}) => (
                        <Typography.Text>{issue.summary}</Typography.Text>
                    )}
                />
                <Column
                    key="milestone"
                    title="Milestone"
                    render={(_, {issue}) => (
                        <GitHubMilestone milestone={issue.milestone}/>
                    )}
                />
                <Column
                    key="assignee"
                    title="Assignee"
                    render={(_, {issue}) => (
                        <GitHubUser user={issue.assignee}/>
                    )}
                />
                <Column
                    key="updatedAt"
                    title="Last update"
                    render={(_, {issue}) =>
                        <TimestampText value={issue.updateTime}/>
                    }
                />
                <Column
                    key="labels"
                    title="Labels"
                    render={(_, {issue}) =>
                        <GitHubLabels labels={issue.labels}/>
                    }
                />
            </Table>
        </>
    )
}