import AceEditor from "react-ace";

export default function Yaml({yaml}) {
    return <AceEditor
        mode="yaml"
        theme="github"
        value={yaml}
        readOnly={true}
        showPrintMargin={false}
        width="100%"
        height="32em"
        setOptions={{
            showLineNumbers: false
        }}
    />
}