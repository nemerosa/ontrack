import ListSection from "@components/common/ListSection";
import {FaWrench} from "react-icons/fa";
import {Dynamic} from "@components/common/Dynamic";

export default function PropertiesSection({loading, entity}) {
    return (
        <ListSection
            icon={<FaWrench/>}
            title="Properties"
            loading={loading}
            items={
                entity.properties
                    .filter(it => it.value)
                    .map(({type, value}) => {
                        const shortTypeName = type.typeName.slice("net.nemerosa.ontrack.extension.".length)
                        return {
                            title: type.name,
                            icon: <Dynamic path={`framework/properties/${shortTypeName}/Icon`}/>,
                            content: <Dynamic path={`framework/properties/${shortTypeName}/Display`} props={{
                                property: {
                                    type,
                                    value,
                                }
                            }}/>,
                        }
                    })
            }
        />
    )
}