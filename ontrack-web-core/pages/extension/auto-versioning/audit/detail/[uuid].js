import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import AutoVersioningAuditEntryView from "@components/extension/auto-versioning/AutoVersioningAuditEntryView";

export default function AutoVersioningAuditDetailPage() {
    const router = useRouter()
    const {uuid} = router.query
    return (
        <>
            <MainLayout>
                <AutoVersioningAuditEntryView uuid={uuid}/>
            </MainLayout>
        </>
    )
}