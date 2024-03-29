import {Space} from "antd";
import JiraIssueStatus from "@components/extension/jira/JiraIssueStatus";
import Link from "next/link";

export default function JiraIssueLink({issue}) {
    return (
        <Link href={issue.url}>{issue.key}</Link>
    )
}