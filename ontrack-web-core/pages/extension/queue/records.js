import StandardPage from "@components/layouts/StandardPage";
import {CloseToHomeCommand} from "@components/common/Commands";
import QueueRecords from "@components/extension/queue/QueueRecords";
import {useRouter} from "next/router";

export default function QueueRecordsPage() {
    const router = useRouter()
    const {id} = router.query
    return (
        <>
            <StandardPage
                pageTitle="Queue records"
                commands={[
                    <CloseToHomeCommand key="home"/>,
                ]}
            >
                <QueueRecords id={id}/>
            </StandardPage>
        </>
    )
}