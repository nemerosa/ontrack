import StandardPage from "@components/layouts/StandardPage";
import {CloseToHomeCommand} from "@components/common/Commands";
import QueueRecords from "@components/extension/queue/QueueRecords";

export default function QueueRecordsPage() {
    return (
        <>
            <StandardPage
                pageTitle="Queue records"
                commands={[
                    <CloseToHomeCommand key="home"/>,
                ]}
            >
                <QueueRecords/>
            </StandardPage>
        </>
    )
}