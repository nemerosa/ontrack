import {Descriptions, Tag, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";

export default function Display({property}) {
    return (
        <Descriptions
            column={12}
            items={[
                {
                    key: 'branchIncludes',
                    label: "Branches to include",
                    children: property.value.branchIncludes.map(pattern => (
                        <>
                            <Tag key={pattern}>{pattern}</Tag>
                        </>
                    )),
                    span: 12,
                },
                {
                    key: 'branchExcludes',
                    label: "Branches to exclude",
                    children: property.value.branchExcludes.map(pattern => (
                        <>
                            <Tag key={pattern}>{pattern}</Tag>
                        </>
                    )),
                    span: 12,
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
                    span: 12,
                },
            ]}
        />
    )
}