import {Dynamic} from "@components/common/Dynamic";

export default function PropertyForm({prefix, property}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)
    return <Dynamic path={`framework/properties/${shortTypeName}/Form`} props={{prefix, property}}/>
}