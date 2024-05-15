import {Select} from "antd";
import {jiraServiceDeskRequestStatuses} from "@components/extension/jira/JiraServiceDeskRequestStatus";

export default function SelectJiraServiceDeskRequestStatus({value, onChange}) {
    return (
        <>
            <Select
                options={jiraServiceDeskRequestStatuses}
                value={value}
                onChange={onChange}
                allowClear={true}
            />
        </>
    )
}