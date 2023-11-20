import useDynamic from "@components/common/Dynamic";

export default function ValidationRunDataForm({dataType}) {
    const shortTypeName = dataType.descriptor.id.slice("net.nemerosa.ontrack.extension.".length)

    return useDynamic({
        path: `framework/validation-run-data-form/${shortTypeName}`,
        errorMessage: `Cannot load validation run data form: ${shortTypeName}`,
        props: {...dataType.config}
    }, [dataType])
}