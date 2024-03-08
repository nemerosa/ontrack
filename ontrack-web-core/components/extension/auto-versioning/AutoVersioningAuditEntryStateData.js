import {Descriptions, Input, Typography} from "antd";

export default function AutoVersioningAuditEntryStateData({data}) {

    const hasData = Object.keys(data).length > 0

    const items = Object.keys(data).map(key => {
        const value = data[key]
        return {
            label: key,
            children: key === 'error' ?
                <Input.TextArea
                    style={{
                        width: '70em',
                    }}
                    value={value}
                    rows={8}
                /> :
                <Typography.Text>{JSON.stringify(value)}</Typography.Text>
        }
    })

    return (
        <>
            {
                !hasData && <Typography.Text type="secondary">No data</Typography.Text>
            }
            {
                hasData && <Descriptions
                    style={{
                        width: 300,
                    }}
                    column={1}
                    layout="vertical"
                    size="small"
                    items={items}
                />
            }
        </>
    )
}