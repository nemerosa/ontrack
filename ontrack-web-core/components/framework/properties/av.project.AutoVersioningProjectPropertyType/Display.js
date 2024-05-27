import {Descriptions, Tag, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";

export default function Display({property}) {
    return (
        <Descriptions
            items={[
                {
                    key: 'branchIncludes',
                    label: "Branches to include",
                    children: property.value.branchIncludes.map(pattern => (
                        <>
                            <Tag key={pattern}>{pattern}</Tag>
                        </>
                    )),
                },
                {
                    key: 'branchExcludes',
                    label: "Branches to exclude",
                    children: property.value.branchExcludes.map(pattern => (
                        <>
                            <Tag key={pattern}>{pattern}</Tag>
                        </>
                    )),
                },
                {
                    key: 'lastActivityDate',
                    label: "Last activity date",
                    children: <TimestampText
                        value={property.value.lastActivityDate}
                        empty={
                            <Typography.Text type="secondary" italic>None</Typography.Text>
                        }
                    />,
                },
            ]}
        />
    )
}