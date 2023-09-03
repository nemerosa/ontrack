import {Popover, Space, Typography} from "antd";
import {PromotionLevelImage} from "@components/common/Links";
import AnnotatedDescription from "@components/common/AnnotatedDescription";

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
                                <AnnotatedDescription entity={promotionLevel}/>
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
                    </Popover> :
                    <PromotionLevelImage
                        promotionLevel={promotionLevel}
                        size={size}
                    />

            }
        </>
    )
}