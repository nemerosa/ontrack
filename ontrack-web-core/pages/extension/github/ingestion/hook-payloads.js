import MainLayout from "@components/layouts/MainLayout";
import GitHubIngestionHookPayloadsView from "@components/extension/github/ingestion/GitHubIngestionHookPayloadsView";

export default function GitHubIngestionHookPayloadsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <GitHubIngestionHookPayloadsView/>
                </MainLayout>
            </main>
        </>
    )
}