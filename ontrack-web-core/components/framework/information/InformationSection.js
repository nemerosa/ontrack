import ListSection from "@components/common/ListSection";
import Information from "@components/framework/information/Information";

export default function InformationSection({entity, loading}) {
    return (
        <>
            {
                entity.information && <ListSection
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