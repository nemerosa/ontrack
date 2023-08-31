import {Popover, Space, Typography} from "antd";
import {PromotionLevelImage} from "@components/common/Links";

export default function PromotionLevel({promotionLevel, details, size = 16, displayTooltip = true}) {
    return (
        <>
            {
                displayTooltip ?
                    <Popover
                        title={
                            <Typography.Text strong>{promotionLevel.name}</Typography.Text>
                        }
                        content={
                            <Space direction="vertical">
                                {
                                    promotionLevel.description &&
                                    <Typography.Text type="secondary">{promotionLevel.description}</Typography.Text>
                                }
                                {
                                    details
                                }
                            </Space>
                        }
                    >
                        <div>
                        </div>
                    </Popover> :
                    <PromotionLevelImage
                        promotionLevel={promotionLevel}
                        size={size}
                    />

            }
        </>
    )
}