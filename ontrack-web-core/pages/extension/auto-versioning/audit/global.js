import StandardPage from "@components/layouts/StandardPage";
import AutoVersioningAuditView from "@components/extension/auto-versioning/AutoVersioningAuditView";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";

export default function AutoVersioningAuditGlobalPage() {
    return (
        <>
            <StandardPage
                pageTitle="Auto-versioning audit"
                breadcrumbs={projectBreadcrumbs()}
            >
                <AutoVersioningAuditView/>
            </StandardPage>
        </>
    )
}