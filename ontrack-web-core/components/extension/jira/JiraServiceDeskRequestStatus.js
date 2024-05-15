export const jiraServiceDeskRequestStatuses = [
    {
        value: 'ALL',
        label: "All requests",
    },
    {
        value: 'OPEN',
        label: "Only open requests",
    },
    {
        value: 'CLOSED',
        label: "Only closed requests",
    },
]

export default function JiraServiceDeskRequestStatus({status}) {
    return (
        <>
            {
                jiraServiceDeskRequestStatuses.find(it => it.value === status)?.label
            }
        </>
    )
}