import GridCell from "@components/grid/GridCell";
import {Dynamic} from "@components/common/Dynamic";
import {useEffect, useState} from "react";
import {useTemplateRenderers} from "@components/extension/issues/SelectTemplateRenderer";
import {Button, Dropdown, Input, Modal, Space, Spin} from "antd";
import {FaCheck, FaCopy, FaDownload, FaTools} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import copy from "copy-to-clipboard";
import CheckStatus from "@components/common/CheckStatus";
import {gql} from "graphql-request";

export default function ChangeLogIssues({id, from, to, issues}) {

    const client = useGraphQLClient()

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
    const [exportedContent, setExportedContent] = useState('')
    const [exportedContentShowing, setExportedContentShowing] = useState(false)
    const [exportCopied, setExportCopied] = useState(false)

    const onExport = () => {
        setExporting(true)
        setExportedContent('')
        setExportCopied(false)

        const grouping = preferences.groups
            .map(({name, list}) => (
                `${name}=${
                    list.map(it => it.mapping).join(',')
                }`
            ))
            .join('|')

        client.request(
            gql`
                query ChangeLogExport(
                    $from: Int!,
                    $to: Int!,
                    $format: String!,
                    $grouping: String,
                    $exclude: String,
                    $altGroup: String,
                ) {
                    scmChangeLog(from: $from, to: $to) {
                        export(
                            request: {
                                format: $format,
                                grouping: $grouping,
                                exclude: $exclude,
                                altGroup: $altGroup,
                            }
                        )
                    }
                }
            `,
            {
                from,
                to,
                format: preferences.format,
                grouping,
                exclude: null, // TODO
                altGroup: null, // TODO
            }
        ).then(data => {
            const content = data.scmChangeLog.export
            setExportedContent(content)
            showExportedContent()
        }).finally(() => {
            setExporting(false)
        })
    }

    const showExportedContent = () => {
        setExportedContentShowing(true)
    }

    const closeExportedContent = () => {
        setExportedContentShowing(false)
    }

    const onCopy = () => {
        if (exportedContent) {
            setExportCopied(copy(exportedContent))
        }
    }

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
                        {/*{*/}
                        {/*    exportCopied &&*/}
                        {/*    <CheckStatus*/}
                        {/*        value={true}*/}
                        {/*        text="Export copied"*/}
                        {/*    />*/}
                        {/*}*/}
                        {/*{*/}
                        {/*    exportedContent && !exportCopied &&*/}
                        {/*    <Button*/}
                        {/*        icon={<FaCopy/>}*/}
                        {/*        onClick={onCopy}*/}
                        {/*    >*/}
                        {/*        Export ready - click to copy*/}
                        {/*    </Button>*/}
                        {/*}*/}
                        <Dropdown.Button
                            type="primary"
                            trigger="click"
                            disabled={exporting}
                            menu={{items}}
                            onClick={onExport}
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

            <Modal title="Exported change log"
                   open={exportedContentShowing}
                   disabled={true}
                   footer={() => (
                       <>
                           <Button
                               icon={exportCopied ? <FaCheck/> : <FaCopy/>}
                               onClick={onCopy}
                               disabled={exportCopied}
                           >
                               {
                                   exportCopied ? 'Copied' : 'Copy'
                               }
                           </Button>
                           <Button
                               id="close-exported-content"
                               onClick={closeExportedContent}
                               type="primary"
                           >
                               Close
                           </Button>
                       </>
                   )}
                   onCancel={closeExportedContent}
                   width="70%"
            >
                <Input.TextArea
                    id="exported-content"
                    placeholder="Exported content"
                    value={exportedContent}
                    rows={20}
                />
            </Modal>

            {/*<IssueChangeLogExportRequestDialog issueChangeLogExportRequestDialog={issueChangeLogExportRequestDialog}/>*/}
        </>
    )
}