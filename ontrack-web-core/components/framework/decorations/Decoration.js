import useDynamic from "@components/common/Dynamic";

export default function Decoration({decoration}) {
    const shortTypeName = decoration.decorationType.slice("net.nemerosa.ontrack.extension.".length)

    return useDynamic({
        path: `framework/decorations/${shortTypeName}`,
        errorMessage: `Cannot load decoration: ${shortTypeName}`,
        props: {decoration}
    })

}