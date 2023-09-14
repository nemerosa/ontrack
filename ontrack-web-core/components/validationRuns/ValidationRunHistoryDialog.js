import {useEffect, useState} from "react";
import {List, Modal, Space, Typography} from "antd";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import BuildLink from "@components/builds/BuildLink";
import ValidationStampLink from "@components/validationStamps/ValidationStampLink";
import PageSection from "@components/common/PageSection";
import ValidationRunLink from "@components/validationRuns/ValidationRunLink";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import Timestamp from "@components/common/Timestamp";
import Rows from "@components/common/Rows";
import ValidationRunStatusChange from "@components/validationRuns/ValidationRunStatusChange";
import {isAuthorized} from "@components/common/authorizations";
import ValidationDataType from "@components/framework/validation-data-type/ValidationDataType";
import InfoBox from "@components/common/InfoBox";
import ValidationRunData from "@components/framework/validation-run-data/ValidationRunData";

export function useValidationRunHistoryDialog() {
    const [open, setOpen] = useState(false)

    const start = () => {
        setOpen(true)
    }

    const close = () => {
        setOpen(false)
    }

    return {
        open, // State of the dialog
        start, // Opens the dialog
        close, // Closes the dialog
    }
}

export default function ValidationRunHistoryDialog({run, dialog, onChange}) {

    const [loading, setLoading] = useState(true)
    const [build, setBuild] = useState()
    const [validationStamp, setValidationStamp] = useState()
    const [pageInfo, setPageInfo] = useState()
    const [runs, setRuns] = useState([])
    const [runsReload, setRunsReload] = useState(0)
    useEffect(() => {
        if (dialog.open) {
            setLoading(true)
            graphQLCall(
                gql`
                    query GetValidationRun($runId: Int!) {
                        validationRuns(id: $runId) {
                            build {
                                id
                                releaseProperty {
                                    value
                                }
                            }
                            validationStamp {
                                name
                            }
                        }
                    }
                `,
                {runId: run.id}
            ).then(data => {
                const buildId = data.validationRuns[0].build.id
                const validationStampName = data.validationRuns[0].validationStamp.name
                return graphQLCall(
                    gql`
                        query GetValidationHistory(
                            $buildId: Int!,
                            $validationStampName: String!,
                            $offset: Int!,
                            $size: Int!,
                        ) {
                            build(id: $buildId) {
                                id
                                name
                                validations(validationStamp: $validationStampName) {
                                    validationStamp {
                                        id
                                        name
                                        image
                                        _image
                                        dataType {
                                            descriptor {
                                                id
                                                feature {
                                                    id
                                                }
                                            }
                                            config
                                        }
                                        validationRunsPaginated(buildId: $buildId, offset: $offset, size: $size) {
                                            pageInfo {
                                                nextPage {
                                                    offset
                                                    size
                                                }
                                            }
                                            pageItems {
                                                id
                                                runOrder
                                                data {
                                                    descriptor {
                                                        id
                                                        feature {
                                                            id
                                                        }
                                                    }
                                                    data
                                                }
                                                authorizations {
                                                    name
                                                    action
                                                    authorized
                                                }
                                                runInfo {
                                                    sourceType
                                                    sourceUri
                                                    triggerType
                                                    triggerData
                                                    runTime
                                                }
                                                lastStatus {
                                                    statusID {
                                                        id
                                                        name
                                                    }
                                                }
                                                validationRunStatuses {
                                                    id
                                                    creation {
                                                        user
                                                        time
                                                    }
                                                    description
                                                    annotatedDescription
                                                    statusID {
                                                        id
                                                        name
                                                    }
                                                    authorizations {
                                                        name
                                                        action
                                                        authorized
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    `, {
                        buildId,
                        validationStampName,
                        offset: 0,
                        size: 10,
                    }
                )
            }).then(data => {
                setBuild(data.build)
                setValidationStamp(data.build.validations[0].validationStamp)
                setPageInfo(data.build.validations[0].validationStamp.validationRunsPaginated.pageInfo)
                setRuns(data.build.validations[0].validationStamp.validationRunsPaginated.pageItems)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [run, dialog.open, runsReload]);

    const onOk = async () => {
        dialog.close()
        if (runsReload > 0 && onChange) {
            onChange()
        }
    }

    const reloadOnStatusChanged = () => {
        setRunsReload(runsReload + 1)
    }

    const replaceStatusCommentInRun = (run, vrsId, description, annotatedDescription) => ({
        ...run,
        validationRunStatuses: run.validationRunStatuses.map(vrs => {
            if (vrs.id === vrsId) {
                return {
                    ...vrs,
                    description,
                    annotatedDescription,
                }
            } else {
                return vrs
            }
        }),
    })

    const replaceStatusCommentInRuns = (runs, vrsId, description, annotatedDescription) => runs.map(run => replaceStatusCommentInRun(run, vrsId, description, annotatedDescription))

    const editStatusComment = async (vrs, text) => {
        const data = await graphQLCall(
            gql`
                mutation ChangeStatusComment(
                    $validationRunStatusId: Int!,
                    $comment: String!,
                ) {
                    changeValidationRunStatusComment(input: {
                        validationRunStatusId: $validationRunStatusId,
                        comment: $comment,
                    }) {
                        validationRun {
                            validationRunStatuses {
                                id
                                description
                                annotatedDescription
                            }
                        }
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                validationRunStatusId: vrs.id,
                comment: text,
            }
        )
        // Gets the text of the changed status
        const newVrs = data.changeValidationRunStatusComment.validationRun
            .validationRunStatuses
            .find(it => it.id === vrs.id)
        // Refreshes only the status
        if (newVrs) {
            const {description, annotatedDescription} = newVrs
            setRuns(replaceStatusCommentInRuns(runs, vrs.id, description, annotatedDescription))
        }
    }

    return (
        <>
            <Modal
                open={dialog.open}
                closable={false}
                destroyOnClose={true}
                cancelButtonProps={{style: {display: 'none'}}}
                onOk={onOk}
                onCancel={onOk}
                width={800}
            >
                <LoadingContainer loading={loading}>
                    <Rows>
                        <Typography.Title level={4}>
                            Runs for <ValidationStampLink validationStamp={validationStamp}/> in build <BuildLink
                            build={build}/>
                        </Typography.Title>

                        {/* Validation stamp data config */}
                        {
                            validationStamp && validationStamp.dataType &&
                            <InfoBox>
                                <ValidationDataType dataType={validationStamp.dataType}/>
                            </InfoBox>
                        }
                        {
                            runs.map(run =>
                                <PageSection
                                    key={run.id}
                                    title={
                                        <Space>
                                            <ValidationRunStatus status={run.lastStatus} displayText={false}
                                                                 tooltip={false}/>
                                            <ValidationRunLink run={run} text={`Run #${run.runOrder}`}/>
                                        </Space>
                                    }
                                    padding={false}
                                >
                                    <Rows>
                                        {/* Adding a comment */}
                                        {
                                            isAuthorized(run, 'validation_run', 'status_change') &&
                                            <ValidationRunStatusChange
                                                run={run}
                                                onStatusChanged={reloadOnStatusChanged}
                                            />
                                        }
                                        {/* Validation run data */}
                                        {
                                            run.data &&
                                            <ValidationRunData data={run.data}/>
                                        }
                                        {/* List of statuses */}
                                        <List
                                            dataSource={run.validationRunStatuses}
                                            renderItem={vrs => (
                                                <List.Item
                                                    key={vrs.id}
                                                    style={{padding: 8, paddingLeft: 24}}
                                                    actions={[
                                                        <Timestamp
                                                            key={vrs.id}
                                                            prefix={
                                                                `${vrs.creation.user} @`
                                                            }
                                                            value={vrs.creation.time}
                                                        />
                                                    ]}
                                                >
                                                    <List.Item.Meta
                                                        title={
                                                            <Space>
                                                                <ValidationRunStatus
                                                                    status={vrs}
                                                                    displayText={false}
                                                                    tooltip={false}
                                                                />
                                                                <Typography.Text
                                                                    strong>{vrs.statusID.name}</Typography.Text>
                                                            </Space>
                                                        }
                                                        description={
                                                            <AnnotatedDescription
                                                                entity={vrs}
                                                                disabled={false}
                                                                editable={isAuthorized(vrs, 'validation_run_status', 'comment_change')}
                                                                onChange={(text) => editStatusComment(vrs, text)}
                                                            />
                                                        }
                                                    />
                                                </List.Item>
                                            )}
                                        >
                                        </List>
                                    </Rows>
                                </PageSection>
                            )
                        }

                    </Rows>
                </LoadingContainer>
            </Modal>
        </>
    )
}