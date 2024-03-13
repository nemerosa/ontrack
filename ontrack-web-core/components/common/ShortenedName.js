import {Typography} from "antd";

export function truncate(text, max = 24, suffixCount = 12) {
    if (text.length > max) {
        // Always keeping (suffix) characters at the end
        const suffix = text.slice(-suffixCount).trim()
        // The start of the text must not exceed (max - suffix - 1)
        const start = text.slice(0, max - suffixCount - 1).trim()
        return `${start}...${suffix}`
    } else {
        return text
    }
}

export default function ShortenedName({text, suffixCount = 12}) {
    let actualText = text
    let suffix = undefined
    if (actualText.length > 12) {
        actualText = text.slice(0, text.length - suffixCount).trim()
        suffix = text.slice(-suffixCount).trim()
    }
    return (
        <Typography.Text
            style={{
                width: '100%',
            }}
            ellipsis={{
                suffix,
            }}
        >
            {actualText}
        </Typography.Text>
    )
}