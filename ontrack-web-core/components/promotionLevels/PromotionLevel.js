import {Popover, Space, Typography} from "antd";
import {PromotionLevelImage} from "@components/common/Links";

export default function PromotionLevel({promotionLevel, details, size = 16}) {
    return (
        <>
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
                    <PromotionLevelImage
                        promotionLevel={promotionLevel}
                        size={size}
                    />
                </div>
            </Popover>
        </>
    )
}