import {Dynamic} from "@components/common/Dynamic";

export default function PropertyForm({prefix, entity, property, form}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)
    return <Dynamic path={`framework/properties/${shortTypeName}/Form`} props={{prefix, property, entity, form}}/>
}