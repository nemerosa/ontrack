import StandardPage from "@components/layouts/StandardPage";
import AutoVersioningAuditView from "@components/extension/auto-versioning/AutoVersioningAuditView";

export default function AutoVersioningAuditGlobalPage() {
    return (
        <>
            <StandardPage
                pageTitle="Auto-versioning audit"
            >
                <AutoVersioningAuditView/>
            </StandardPage>
        </>
    )
}