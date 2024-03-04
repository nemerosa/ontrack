import {Dynamic} from "@components/common/Dynamic";

export default function ValidationDataType({dataType}) {

    const shortTypeName = dataType.descriptor.id.slice("net.nemerosa.ontrack.extension.".length)

    return <Dynamic
        path={`framework/validation-data-type/${shortTypeName}`}
        errorMessage={`Cannot load validation data type: ${shortTypeName}`}
        props={{...dataType.config}}
    />

}