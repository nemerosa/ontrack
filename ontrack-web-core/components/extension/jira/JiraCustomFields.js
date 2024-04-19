import {Descriptions, Typography} from "antd";

export default function JiraCustomFields({customFields}) {
    return customFields && customFields.length > 0 ?
        <Descriptions
            column={12}
            items={
                customFields.map(({name, value}) => ({
                    key: name,
                    label: name,
                    children: <Typography.Text code>{JSON.stringify(value, null, 2)}</Typography.Text>,
                    span: 12,
                }))
            }
        /> :
        <Typography.Text type="secondary">None</Typography.Text>
}