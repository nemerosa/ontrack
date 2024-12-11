import {getTextColorForBackground, numberToColorHsl} from "@components/common/colors/Colors";
import {Tooltip} from "antd";

export default function GeneratedIcon({name, colorIndex, onClick, tooltipText}) {

    const initials = generateInitials(name)
    const bgColor = numberToColorHsl(colorIndex)
    const textColor = getTextColorForBackground(bgColor)

    return (
        <>
            <Tooltip title={tooltipText}>
                <span
                    style={{
                        display: 'inline-block',
                        width: '2.1em',
                        height: '2.1em',
                        lineHeight: '2.1em',
                        textAlign: 'center',
                        backgroundColor: bgColor,
                        color: textColor,
                        // Adjust font-size slightly smaller so both letters fit nicely.
                        fontSize: '0.75em',
                        // Optionally a small border radius to smooth corners, but it's optional.
                        borderRadius: '0.1em',
                        verticalAlign: 'middle'
                    }}
                    onClick={onClick}
                    className={onClick ? 'ot-action' : undefined}
                >
                  {initials}
                </span>
            </Tooltip>
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