import GridCell from "@components/grid/GridCell";
import {Dynamic} from "@components/common/Dynamic";
import {useEffect, useState} from "react";
import {useIssueExportFormats} from "@components/extension/issues/SelectIssueExportFormat";

export default function ChangeLogIssues({id, issues}) {

    const [issueServiceId, setIssueServiceId] = useState('')
    useEffect(() => {
        if (issues) {
            setIssueServiceId(issues.issueServiceConfiguration.serviceId)
        }
    }, [issues])

    // const client = useGraphQLClient()

    const exportFormats = useIssueExportFormats()
    //
    // const [preferences, setPreferences] = useState({
    //     format: 'text',
    //     groups: [],
    //     exclude: '',
    //     altGroup: '',
    // })
    //
    // useEffect(() => {
    //     const storedPreferences = localStorage.getItem('change-log-issues-export')
    //     if (storedPreferences) {
    //         setPreferences(JSON.parse(storedPreferences))
    //     }
    // }, []);
    //
    // const savePreferences = (values) => {
    //     setPreferences(values)
    //     localStorage.setItem('change-log-issues-export', JSON.stringify(values))
    // }
    //
    // const selectFormat = (id) => {
    //     const newPreferences = {
    //         ...preferences,
    //         format: id,
    //     }
    //     savePreferences(newPreferences)
    // }
    //
    // const [items, setItems] = useState([])
    //
    // useEffect(() => {
    //     const items = []
    //
    //     exportFormats.forEach(exportFormat => {
    //         items.push({
    //             key: exportFormat.id,
    //             label: <Space>
    //                 {
    //                     exportFormat.id === preferences.format && <FaCheck/>
    //                 }
    //                 {exportFormat.name}
    //             </Space>,
    //             onClick: () => selectFormat(exportFormat.id)
    //         })
    //     })
    //
    //     items.push({type: 'divider'})
    //     items.push({
    //         key: 'options',
    //         label: "Options...",
    //         icon: <FaTools/>,
    //         onClick: startIssueChangeLogExportRequestDialog,
    //     })
    //
    //     setItems(items)
    //
    // }, [exportFormats, preferences]);
    //
    // const [exporting, setExporting] = useState(false)
    // const [exportedContent, setExportedContent] = useState('')
    // const [exportCopied, setExportCopied] = useState(false)
    //
    // const onExport = () => {
    //     setExporting(true)
    //     setExportedContent('')
    //     setExportCopied(false)
    //
    //     const grouping = preferences.groups
    //         .map(({name, list}) => (
    //             `${name}=${
    //                 list.map(it => it.mapping).join(',')
    //             }`
    //         ))
    //         .join('|')
    //
    //     client.request(
    //         gql`
    //             query ChangeLogExport(
    //                 $uuid: String!,
    //                 $format: String!,
    //                 $grouping: String!,
    //             ) {
    //                 gitChangeLogByUUID(uuid: $uuid) {
    //                     export(request: {
    //                         format: $format,
    //                         grouping: $grouping,
    //                     })
    //                 }
    //             }
    //         `,
    //         {
    //             uuid: changeLogUuid,
    //             format: preferences.format,
    //             grouping,
    //         }
    //     ).then(data => {
    //         const content = data.gitChangeLogByUUID.export
    //         setExportedContent(content)
    //     }).finally(() => {
    //         setExporting(false)
    //     })
    // }
    //
    // const onCopy = () => {
    //     if (exportedContent) {
    //         setExportCopied(copy(exportedContent))
    //     }
    // }
    //
    // const issueChangeLogExportRequestDialog = useIssueChangeLogExportRequestDialog({
    //     onSuccess: (values) => {
    //         savePreferences(values)
    //     }
    // })
    //
    // const startIssueChangeLogExportRequestDialog = () => {
    //     issueChangeLogExportRequestDialog.start(preferences)
    // }

    return (
        <>
            <GridCell
                id={id}
                title="Issues"
                padding={0}
                // extra={
                //     <>
                //         {
                //             exportCopied &&
                //             <CheckStatus
                //                 value={true}
                //                 text="Export copied"
                //             />
                //         }
                //         {
                //             exportedContent && !exportCopied &&
                //             <Button
                //                 icon={<FaCopy/>}
                //                 onClick={onCopy}
                //             >
                //                 Export ready - click to copy
                //             </Button>
                //         }
                //         <Dropdown.Button
                //             type="primary"
                //             trigger="click"
                //             disabled={loading || exporting}
                //             menu={{items}}
                //             onClick={onExport}
                //         >
                //             <Space>
                //                 {exporting ? <Spin size="small"/> : <FaDownload/>}
                //                 Export
                //             </Space>
                //         </Dropdown.Button>
                //     </>
                // }
            >
                {
                    issueServiceId &&
                    <Dynamic
                        path={`framework/issues/${issueServiceId}-issues`}
                        props={{issues: issues.issues}}
                    />
                }
            </GridCell>
            {/*<IssueChangeLogExportRequestDialog issueChangeLogExportRequestDialog={issueChangeLogExportRequestDialog}/>*/}
        </>
    )
}