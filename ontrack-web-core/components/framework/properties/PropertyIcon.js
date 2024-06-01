import {Dynamic} from "@components/common/Dynamic";

export default function PropertyIcon({property}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)
    return <Dynamic path={`framework/properties/${shortTypeName}/Icon`}/>
}