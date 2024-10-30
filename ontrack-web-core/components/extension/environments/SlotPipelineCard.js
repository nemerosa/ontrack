import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";
import TimestampText from "@components/common/TimestampText";
import {Card, Descriptions, Space} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

const fullCardTitle = (prefix, title) => {
    if (prefix) {
        return `${prefix} ${title}`
    } else {
        return title
    }
}

export default function SlotPipelineCard({pipeline, actions = true, titlePrefix, onChange}) {
    const items = []
    items.push({
        key: 'status',
        label: 'Status',
        children: <SlotPipelineStatusActions
            pipeline={pipeline}
            actions={actions}
            onChange={onChange}
        />,
        span: 12,
    })
    items.push({
        key: 'start',
        label: 'Started at',
        children: <TimestampText value={pipeline.start}/>,
        span: 12,
    })
    if (pipeline.end) {
        items.push({
            key: 'end',
            label: 'Ended at',
            children: <TimestampText value={pipeline.end}/>,
            span: 12,
        })
    }
    items.push({
        key: 'build',
        label: 'Build',
        children: <Space>
            <BuildLink build={pipeline.build}/>
            <PromotionRuns promotionRuns={pipeline.build.promotionRuns}/>
        </Space>,
        span: 12,
    })

    return (
        <>
            <Card
                title={fullCardTitle(titlePrefix, `Pipeline #${pipeline.number}`)}
                size="small"
                hoverable={true}
                data-testid={pipeline.id}
            >
                <Descriptions
                    items={items}
                />
            </Card>
        </>
    )
}