import {Space} from "antd";
import JiraIssueLink from "@components/extension/jira/JiraIssueLink";

export default function JiraIssueLinks({links = []}) {
    return (
        <>
            <Space size={2}>
                {
                    links.map((link, index) => (
                        <Space size={2} key={`link-${index}`}>
                            {link.link}
                            <JiraIssueLink issue={link}/>
                        </Space>
                    ))
                }
            </Space>
        </>
    )
}