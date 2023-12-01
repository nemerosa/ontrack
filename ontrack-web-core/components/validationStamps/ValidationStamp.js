import {Popover, Space, Typography} from "antd";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {legacyValidationStampUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import LegacyLink from "@components/common/LegacyLink";

function CoreValidationStamp({
                                 validationStamp,
                                 text, displayText = true,
                                 displayImage = true,
                                 imageSize = 16,
                                 selectable,
                                 selected,
                                 onClick,
                             }) {
    return (
        <Space
            onClick={onClick}
            className={onClick ? "ot-action" : undefined}
            style={{
                opacity: !selectable || selected ? undefined : '20%'
            }}
        >
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

function LinkedValidationStamp({
                                   validationStamp,
                                   text, displayText = true,
                                   displayImage = true,
                                   imageSize = 16,
                                   link, displayLink = true,
                                   selectable,
                                   selected,
                                   onClick,
                               }) {
    const core = <CoreValidationStamp validationStamp={validationStamp}
                                      text={text} displayText={displayText}
                                      displayImage={displayImage}
                                      imageSize={imageSize}
                                      onClick={onClick}
                                      selectable={selectable}
                                      selected={selected}
    />
    if (displayLink) {
        return <LegacyLink href={link ? link : legacyValidationStampUri(validationStamp)}>
            {core}
        </LegacyLink>
    } else {
        return core
    }
}

export default function ValidationStamp({
                                            validationStamp,
                                            text, displayText = true,
                                            displayImage = true, imageSize = 16,
                                            link, displayLink = true,
                                            tooltip, displayTooltip = true, tooltipPlacement,
                                            onClick,
                                            selectable,
                                            selected,
                                        }) {

    const innerLink = <LinkedValidationStamp
        validationStamp={validationStamp}
        text={text} displayText={displayText}
        displayImage={displayImage}
        imageSize={imageSize}
        link={link} displayLink={displayLink}
        onClick={onClick}
        selectable={selectable}
        selected={selected}
    />

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
                {innerLink}
            </span>
        </Popover>
    } else {
        return innerLink
    }

}