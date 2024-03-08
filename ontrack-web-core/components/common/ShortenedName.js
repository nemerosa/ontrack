import {Typography} from "antd";

export default function ShortenedName({text, suffixCount = 12}) {
    const start = text.slice(0, text.length - suffixCount);
    const suffix = text.slice(-suffixCount).trim();
    return (
        <Typography.Text
            style={{
                width: '100%',
            }}
            ellipsis={{
                suffix,
            }}
        >
            {start}
        </Typography.Text>
    )
}