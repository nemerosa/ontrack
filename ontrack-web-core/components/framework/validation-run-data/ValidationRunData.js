import {Dynamic} from "@components/common/Dynamic";

export default function ValidationRunData({data}) {
    const shortTypeName = data ? data.descriptor.id.slice("net.nemerosa.ontrack.extension.".length) : ''

    return <Dynamic
        path={`framework/validation-run-data/${shortTypeName}`}
        props={{...data.data}}
    />
}