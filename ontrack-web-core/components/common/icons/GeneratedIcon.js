import {getTextColorForBackground, numberToColorHsl} from "@components/common/colors/Colors";
import {Tooltip} from "antd";
import {actionClassName} from "@components/common/ClassUtils";

export default function GeneratedIcon({name, colorIndex, onClick, tooltipText, size = 24, disabled = false}) {

    const initials = name && generateInitials(name)
    const bgColor = numberToColorHsl(colorIndex)
    const textColor = getTextColorForBackground(bgColor)

    const fontSize = size * 0.5

    return (
        <>
            {
                name &&
                <Tooltip title={tooltipText}>
                <span
                    style={{
                        display: 'inline-block',
                        width: `${size}px`,
                        maxWidth: `${size}px`,
                        height: `${size}px`,
                        maxHeight: `${size}px`,
                        lineHeight: `${size}px`,
                        textAlign: 'center',
                        backgroundColor: bgColor,
                        color: textColor,
                        // Adjust font-size slightly smaller so both letters fit nicely.
                        fontSize: `${fontSize}px`,
                        // Optionally a small border radius to smooth corners, but it's optional.
                        borderRadius: '0.1em',
                        verticalAlign: 'middle',
                        filter: disabled ? 'grayscale(100%)' : undefined,
                    }}
                    onClick={onClick}
                    className={actionClassName(onClick, disabled)}
                >
                  {initials}
                </span>
                </Tooltip>
            }
        </>
    )
}

export const generateInitials = (name) => {
    const parts = name.split('-')

    // If there's only one part, use the first two letters of it
    if (parts.length === 1) {
        return parts[0].substring(0, 2).toUpperCase()
    }

    // If there are multiple parts, use the first letter of the first two parts
    return parts
        .slice(0, 2) // In case there are more than two parts, just take the first two
        .map(part => part.charAt(0).toUpperCase())
        .join('')
}