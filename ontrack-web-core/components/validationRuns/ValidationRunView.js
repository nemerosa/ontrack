import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {buildKnownName, pageTitle, validationStampTitleName} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {Space, Typography} from "antd";
import ValidationStampLink from "@components/validationStamps/ValidationStampLink";
import {downToBuildBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {buildUri} from "@components/common/Links";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import LoadingContainer from "@components/common/LoadingContainer";
import GridCell from "@components/grid/GridCell";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import StoredGridLayout from "@components/grid/StoredGridLayout";
import StoredGridLayoutResetCommand from "@components/grid/StoredGridLayoutResetCommand";
import {gqlValidationRunContent} from "@components/validationRuns/ValidationRunGraphQLFragments";
import ValidationRunStatusList from "@components/validationRuns/ValidationRunStatusList";

export default function ValidationRunView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [run, setRun] = useState({})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query GetValidationRun($id: Int!) {
                        validationRuns(id: $id) {
                            ...ValidationRunContent
                            validationStamp {
                                id
                                name
                                image
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                            }
                            build {
                                id
                                name
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                                releaseProperty {
                                    value
                                }
                            }
                        }
                    }
                    ${gqlValidationRunContent}
                `,
                {id}
            ).then(data => {
                const run = data.validationRuns[0]
                setRun(run)
                setCommands([
                    <StoredGridLayoutResetCommand key="reset"/>,
                    <CloseCommand key="close" href={buildUri(run.build)}/>,
                ])
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id])

    const tableRunStatuses = "table-run-statuses"

    const defaultLayout = [
        {i: tableRunStatuses, x: 0, y: 0, w: 12, h: 12},
    ]

    const items = [
        {
            id: tableRunStatuses,
            content: <GridCell
                id={tableRunStatuses}
                title="Statuses"
            >
                <ValidationRunStatusList
                    run={run}
                />
            </GridCell>,
        },
    ]

    return (
        <>
            <Head>
                {
                    run?.validationStamp &&
                    run?.build &&
                    pageTitle(`${validationStampTitleName(run.validationStamp)} --> ${buildKnownName(run.build)}`)
                }
            </Head>
            <StoredGridLayoutContextProvider>
                <MainPage
                    title={
                        run?.validationStamp && <>
                            <Space>
                                <Typography.Text>Validation to</Typography.Text>
                                <ValidationStampLink validationStamp={run.validationStamp}/>
                            </Space>
                        </>
                    }
                    commands={commands}
                    breadcrumbs={downToBuildBreadcrumbs(run)}
                >
                    <LoadingContainer loading={loading}>
                        <Space direction="vertical" className="ot-line">
                            <AnnotatedDescription entity={run}/>
                            <StoredGridLayout
                                id="page-validation-run-layout"
                                defaultLayout={defaultLayout}
                                items={items}
                                rowHeight={30}
                            />
                        </Space>
                    </LoadingContainer>
                </MainPage>
            </StoredGridLayoutContextProvider>
        </>
    )
}