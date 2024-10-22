import {getExtensionShortName} from "@components/common/ExtensionUtils";

export default function Decoration({decoration}) {
    const shortName = getExtensionShortName(decoration.decorationType)
    // return <Dynamic
    //     path={`framework/decorations/${shortName}`}
    //     props={{decoration}}
    // />

}