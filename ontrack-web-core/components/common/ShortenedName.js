import {Typography} from "antd";

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