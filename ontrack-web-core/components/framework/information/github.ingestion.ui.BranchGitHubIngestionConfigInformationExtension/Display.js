import Yaml from "@components/common/Yaml";

export default function BranchGitHubIngestionConfigInformationExtension({info}) {
    return <Yaml yaml={info.data.yaml}/>
}