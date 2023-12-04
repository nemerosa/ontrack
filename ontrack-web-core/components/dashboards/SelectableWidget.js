import {Button, Card, Tooltip, Typography} from "antd";
import {FaPlus} from "react-icons/fa";

export default function SelectableWidget({widgetDef, addWidget}) {
    return (
        <>
            <Card
                title={widgetDef.name}
                extra={
                    <Tooltip key="add" title={`Add the "${widgetDef.name}" widget to the dashboard`}>
                        <Button type="primary" onClick={() => addWidget(widgetDef)} icon={<FaPlus/>}/>
                    </Tooltip>
                }
            >
                <p>
                    <Typography.Text type="secondary">{widgetDef.description}</Typography.Text>
                </p>
            </Card>
        </>
    )
}