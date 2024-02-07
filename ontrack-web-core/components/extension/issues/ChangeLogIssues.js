import GridCell from "@components/grid/GridCell";
import {Dynamic} from "@components/common/Dynamic";
import {useEffect, useState} from "react";
import {useTemplateRenderers} from "@components/extension/issues/SelectIssueExportFormat";
import {Dropdown, Space, Spin} from "antd";
import {FaCheck, FaDownload, FaTools} from "react-icons/fa";

export default function ChangeLogIssues({id, issues}) {

    const [issueServiceId, setIssueServiceId] = useState('')
    useEffect(() => {
        if (issues) {
            setIssueServiceId(issues.issueServiceConfiguration.serviceId)
        }
    }, [issues])

    // const client = useGraphQLClient()

    const templateRenderers = useTemplateRenderers()

    const [preferences, setPreferences] = useState({
        format: 'text',
        groups: [],
        exclude: '',
        altGroup: '',
    })

    useEffect(() => {
        const storedPreferences = localStorage.getItem('change-log-issues-export')
        if (storedPreferences) {
            setPreferences(JSON.parse(storedPreferences))
        }
    }, []);

    const savePreferences = (values) => {
        setPreferences(values)
        localStorage.setItem('change-log-issues-export', JSON.stringify(values))
    }

    const selectFormat = (id) => {
        const newPreferences = {
            ...preferences,
            format: id,
        }
        savePreferences(newPreferences)
    }

    const [items, setItems] = useState([])

    useEffect(() => {
        const items = []

        templateRenderers.forEach(renderer => {
            items.push({
                key: renderer.id,
                label: <Space>
                    {
                        renderer.id === preferences.format && <FaCheck/>
                    }
                    {renderer.name}
                </Space>,
                onClick: () => selectFormat(renderer.id)
            })
        })

        items.push({type: 'divider'})
        items.push({
            key: 'options',
            label: "Options...",
            icon: <FaTools/>,
            // TODO onClick: startIssueChangeLogExportRequestDialog,
        })

        setItems(items)

    }, [templateRenderers, preferences]);

    const [exporting, setExporting] = useState(false)
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
                extra={
                    <>
                        {/*//         {*/}
                        {/*//             exportCopied &&*/}
                        {/*//             <CheckStatus*/}
                        {/*//                 value={true}*/}
                        {/*//                 text="Export copied"*/}
                        {/*//             />*/}
                        {/*//         }*/}
                        {/*//         {*/}
                        {/*//             exportedContent && !exportCopied &&*/}
                        {/*//             <Button*/}
                        {/*//                 icon={<FaCopy/>}*/}
                        {/*//                 onClick={onCopy}*/}
                        {/*//             >*/}
                        {/*//                 Export ready - click to copy*/}
                        {/*//             </Button>*/}
                        {/*//         }*/}
                        <Dropdown.Button
                            type="primary"
                            trigger="click"
                            disabled={exporting}
                            menu={{items}}
                            // onClick={onExport}
                        >
                            <Space>
                                {exporting ? <Spin size="small"/> : <FaDownload/>}
                                Export
                            </Space>
                        </Dropdown.Button>
                    </>
                }
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