import {Dynamic} from "@components/common/Dynamic";

export default function ValidationRunData({data}) {
    const shortTypeName = data ? data.descriptor.id.slice("net.nemerosa.ontrack.extension.".length) : ''

    if (shortTypeName) {
        const actualData = data.data
        let props = {}
        if (typeof actualData === 'object') {
            props = {...actualData}
        } else {
            props = {value: actualData}
        }

        return <Dynamic
            path={`framework/validation-run-data/${shortTypeName}`}
            props={props}
        />
    } else {
        return ''
    }
}