import {Descriptions} from "antd";
import JsonDisplay from "@components/common/JsonDisplay";

export default function QueueRecordSource({record}) {
    return (
        <>
            <Descriptions
                column={12}
                layout="vertical"
                items={[
                    {
                        key: 'feature',
                        label: "Feature",
                        span: 12,
                        children: record.source?.feature,
                    },
                    {
                        key: 'id',
                        label: "ID",
                        span: 12,
                        children: record.source?.id,
                    },
                    {
                        key: 'data',
                        label: "Data",
                        span: 12,
                        children: <JsonDisplay
                            value={JSON.stringify(record.source?.data, null, 2)}
                            height="8em"
                        />,
                    },
                ]}
            />
        </>
    )
}