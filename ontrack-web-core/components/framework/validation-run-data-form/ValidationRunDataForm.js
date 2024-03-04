import {Dynamic} from "@components/common/Dynamic";

export default function ValidationRunDataForm({dataType}) {
    const shortTypeName = dataType.descriptor.id.slice("net.nemerosa.ontrack.extension.".length)

    return <Dynamic
        path={`framework/validation-run-data-form/${shortTypeName}`}
        props={{...dataType.config}}
    />
}