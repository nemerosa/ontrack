import ListSection from "@components/common/ListSection";
import {FaInfoCircle} from "react-icons/fa";
import {Dynamic} from "@components/common/Dynamic";

export default function InformationSection({entity, loading}) {
    return (
        <>
            {
                entity.information && <ListSection
                    icon={<FaInfoCircle/>}
                    title="Information"
                    loading={loading}
                    items={
                        entity.information
                            .filter(it => it.data)
                            .map(info => {
                                    const shortTypeName = info.type.slice("net.nemerosa.ontrack.extension.".length)
                                    return {
                                        title: info.title,
                                        icon: <Dynamic path={`framework/information/${shortTypeName}/Icon`}/>,
                                        content: <Dynamic path={`framework/information/${shortTypeName}/Display`} props={{info, entity}}/>,
                                    }
                                }
                            )
                    }
                />
            }
        </>
    )
}