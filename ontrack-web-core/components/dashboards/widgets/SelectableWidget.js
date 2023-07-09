import {Card, Tooltip, Typography} from "antd";
import {FaPlus} from "react-icons/fa";

export default function SelectableWidget({widgetDef, addWidget}) {
    return (
        <>
            <Card
                title={widgetDef.name}
                actions={[
                    <Tooltip title={`Add the "${widgetDef.name}" widget to the dashboard`}>
                        <FaPlus onClick={addWidget(widgetDef)}/>
                    </Tooltip>
                ]}
            >
                <p>
                    <Typography.Text type="secondary">{widgetDef.description}</Typography.Text>
                </p>
            </Card>
        </>
    )
}