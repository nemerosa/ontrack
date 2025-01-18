import AceEditor from "react-ace";

export default function Yaml({yaml, height = '32em', showLineNumbers = false}) {
    return <AceEditor
        mode="yaml"
        theme="github"
        value={yaml}
        readOnly={true}
        showPrintMargin={false}
        width="100%"
        height={height}
        setOptions={{
            showLineNumbers: showLineNumbers
        }}
    />
}