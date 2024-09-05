import {Descriptions, Typography} from "antd";

export default function AutoVersioningConfigPath({configPath}) {

    const items = [
        {
            key: 'path',
            label: "Path",
            children: <Typography.Text code>{configPath.path}</Typography.Text>
        },
        {
            key: 'propertyType',
            label: "Type",
            children: <Typography.Text code>{configPath.propertyType}</Typography.Text>
        },
        {
            key: 'regex',
            label: "Regex",
            children: <Typography.Text code>{configPath.regex}</Typography.Text>
        },
        {
            key: 'property',
            label: "Property",
            children: <Typography.Text code>{configPath.property}</Typography.Text>
        },
        {
            key: 'propertyRegex',
            label: "Property regex",
            children: <Typography.Text code>{configPath.propertyRegex}</Typography.Text>
        },
        {
            key: 'versionSource',
            label: "Version source",
            children: <Typography.Text code>{configPath.versionSource}</Typography.Text>
        },
    ]

    return (
        <>
            <Descriptions
                column={1}
                size="small"
                items={items}
            />
        </>
    )
}