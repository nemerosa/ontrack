import Section from "@components/common/Section";

export default function BranchGitHubIngestionConfigInformationExtension({info}) {
    return (
        <>
            <Section title="GitHub Ingestion Config">
                {JSON.stringify(info.data.yaml)}
            </Section>
        </>
    )
}