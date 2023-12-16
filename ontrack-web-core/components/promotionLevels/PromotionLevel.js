import {Popover, Space, Typography} from "antd";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";

export default function PromotionLevel({
                                           promotionLevel, details, size = 16,
                                           displayTooltip = true, displayText = false
                                       }) {

    return (
        <>
            {
                displayTooltip ?
                    <Popover
                        title={
                            <PromotionLevelLink promotionLevel={promotionLevel}/>
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
                        <Space>
                            <PromotionLevelImage
                                promotionLevel={promotionLevel}
                                size={size}
                            />
                            {
                                displayText && <Typography.Text>{promotionLevel.name}</Typography.Text>
                            }
                        </Space>
                    </Popover> :
                    <Space>
                        <PromotionLevelImage
                            promotionLevel={promotionLevel}
                            size={size}
                        />
                        {
                            displayText && <Typography.Text>{promotionLevel.name}</Typography.Text>
                        }
                    </Space>

            }
        </>
    )
}