import ListSection from "@components/common/ListSection";
import Information from "@components/framework/information/Information";
import {FaInfoCircle} from "react-icons/fa";

export default function InformationSection({entity, loading}) {
    return (
        <>
            {
                entity.information && <ListSection
                    icon={<FaInfoCircle/>}
                    title="Information"
                    loading={loading}
                    items={entity.information.filter(it => it.data)}
                    renderItem={(info) => (
                        <Information info={info}/>
                    )}
                />
            }
        </>
    )
}