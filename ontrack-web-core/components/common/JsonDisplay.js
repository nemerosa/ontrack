import AceEditor from "react-ace";

export default function JsonDisplay({value, width = "100%"}) {
    return <AceEditor
        mode="json"
        theme="github"
        value={value}
        readOnly={true}
        showPrintMargin={false}
        width={width}
        height="32em"
        setOptions={{
            showLineNumbers: false,
        }}
    />
}