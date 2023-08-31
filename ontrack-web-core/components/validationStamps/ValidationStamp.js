import {Popover, Space, Typography} from "antd";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {validationStampUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import LegacyLink from "@components/common/LegacyLink";

export default function ValidationStamp({
                                            validationStamp,
                                            text, displayText = true,
                                            displayImage = true, imageSize = 16,
                                            link, displayLink = true,
                                            tooltip, displayTooltip = true, tooltipPlacement
                                        }) {

    function InnerValidationStamp({
                                      validationStamp,
                                      text, displayText = true,
                                      displayImage = true,
                                  }) {
        return (
            <Space>
                {
                    displayImage && <ValidationStampImage validationStamp={validationStamp} size={imageSize}/>
                }
                {
                    displayText && <Typography.Text>
                        {text ? text : validationStamp.name}
                    </Typography.Text>
                }
            </Space>
        )
    }

    function InnerValidationStampLink({
                                          validationStamp,
                                          text, displayText = true,
                                          displayImage = true,
                                          link, displayLink = true
                                      }) {
        const inner = <InnerValidationStamp validationStamp={validationStamp}
                                            text={text} displayText={displayText}
                                            displayImage={displayImage}/>
        if (displayLink) {
            return <LegacyLink href={validationStampUri(validationStamp)}>
                {inner}
            </LegacyLink>
        } else {
            return inner
        }
    }

    const inner = <InnerValidationStampLink validationStamp={validationStamp}
                                            text={text} displayText={displayText}
                                            displayImage={displayImage}
                                            link={link} displayLink={displayLink}/>

    if (displayTooltip) {
        const tooltipContent = tooltip ? tooltip : <Space direction="vertical">
            <Typography.Text>{validationStamp.name}</Typography.Text>
            <AnnotatedDescription entity={validationStamp}/>
        </Space>
        return <Popover
            content={tooltipContent}
            placement={tooltipPlacement}
        >
            <span>
                {inner}
            </span>
        </Popover>
    } else {
        return inner
    }

}