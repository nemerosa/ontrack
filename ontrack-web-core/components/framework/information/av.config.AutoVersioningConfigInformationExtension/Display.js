import Yaml from "@components/common/Yaml";

export default function AutoVersioningConfigInformationExtension({info}) {
    return <Yaml yaml={info.data.yaml}/>
}