import useDynamic from "@components/common/Dynamic";

export default function Information({info}) {

    const shortTypeName = info.type.slice("net.nemerosa.ontrack.extension.".length)

    return useDynamic({
        path: `framework/information/${shortTypeName}`,
        errorMessage: `Cannot load information ${shortTypeName}`,
        props: {info}
    })
}