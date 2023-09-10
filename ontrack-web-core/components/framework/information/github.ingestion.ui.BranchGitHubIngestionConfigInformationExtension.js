import Section from "@components/common/Section";
import AceEditor from 'react-ace';
import "ace-builds/src-noconflict/mode-yaml";
import "ace-builds/src-noconflict/theme-github";

export default function BranchGitHubIngestionConfigInformationExtension({info}) {
    return (
        <>
            <Section title="GitHub Ingestion Config">
                <AceEditor
                    mode="yaml"
                    theme="github"
                    value={info.data.yaml}
                    readOnly={true}
                    showPrintMargin={false}
                    width="100%"
                    height="32em"
                    setOptions={{
                        showLineNumbers: false
                    }}
                />
            </Section>
        </>
    )
}