export default function PaddedContent({padding = 16, children}) {
    return (
        <div
            style={{
                padding: padding,
            }}
        >
            {children}
        </div>
    )
}