import {Dynamic} from "@components/common/Dynamic";

export default function PropertyComponent({property, entityType, entityId}) {
    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)
    return <Dynamic
        path={`framework/properties/${shortTypeName}/Display`}
        props={{property, entityType, entityId}}
    />
}