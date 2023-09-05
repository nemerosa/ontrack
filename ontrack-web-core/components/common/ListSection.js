import PageSection from "@components/common/PageSection";
import {List} from "antd";

export default function ListSection({title, loading, items, renderItem}) {
    return (
        <>
            <PageSection
                title={title}
                loading={loading}
                padding={false}
            >
                <List
                    dataSource={items}
                    renderItem={(item) => renderItem && renderItem(item)}
                    />
            </PageSection>
        </>
    )
}