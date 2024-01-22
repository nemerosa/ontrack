import {Descriptions, Space, Tooltip} from "antd";
import BuildRef from "@components/links/BuildRef";
import BuildPromotions from "@components/links/BuildPromotions";
import {useEffect, useState} from "react";
import CheckStatus from "@components/common/CheckStatus";

export default function LatestLinkInfo({sourceBuild, latestOk, targetBuild}) {

    const [items, setItems] = useState([])
    useEffect(() => {
        setItems([
            {
                key: "source",
                label: <Tooltip title="Latest known parent">Parent</Tooltip>,
                span: 12,
                children: <Space>
                    <BuildRef build={sourceBuild}/>
                    <BuildPromotions build={sourceBuild}/>
                </Space>
            },
            {
                key: "status",
                label: "Status",
                span: 12,
                children: <CheckStatus
                    value={latestOk}
                    text="Using latest"
                    noText="Not using latest"
                />
            },
            {
                key: "target",
                label: <Tooltip title="Latest known child">Child</Tooltip>,
                span: 12,
                children: <Space>
                    <BuildRef build={targetBuild}/>
                    <BuildPromotions build={targetBuild}/>
                </Space>
            },
        ])
    }, [sourceBuild, latestOk, targetBuild]);

    return (
        <Descriptions
            items={items}
            size="small"
        />
    )
}