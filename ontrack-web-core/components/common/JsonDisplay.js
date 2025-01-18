import AceEditor from "react-ace";

export default function JsonDisplay({value, width = "100%", height = "32em", showLineNumbers = false}) {
    return <AceEditor
        mode="json"
        theme="github"
        value={value}
        readOnly={true}
        showPrintMargin={false}
        width={width}
        height={height}
        setOptions={{
            showLineNumbers: showLineNumbers,
            useWorker: false,
        }}
    />
}