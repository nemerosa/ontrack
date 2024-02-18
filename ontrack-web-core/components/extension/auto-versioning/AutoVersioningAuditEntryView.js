import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {Empty, Skeleton, Space} from "antd";
import MainPage from "@components/layouts/MainPage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import AutoVersioningAuditEntry from "@components/extension/auto-versioning/AutoVersioningAuditEntry";

export default function AutoVersioningAuditEntryView({uuid}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [entry, setEntry] = useState({})
    useEffect(() => {
        if (client && uuid) {
            setLoading(true)
            client.request(
                gql`
                    query GetAVAuditDetail(
                        $uuid: String!,
                    ) {
                        autoVersioningAuditEntries(filter: {uuid: $uuid}) {
                            pageItems {
                                mostRecentState {
                                    creation {
                                        time
                                    }
                                    state
                                    data
                                }
                                duration
                                running
                                audit {
                                    creation {
                                        time
                                    }
                                    state
                                    data
                                }
                                routing
                                queue
                                order {
                                    uuid
                                    sourceProject
                                    branch {
                                        name
                                        project {
                                            id
                                            name
                                        }
                                    }
                                    repositoryHtmlURL
                                    targetPaths
                                    targetRegex
                                    targetProperty
                                    targetPropertyRegex
                                    targetPropertyType
                                    targetVersion
                                    autoApproval
                                    autoApprovalMode
                                    upgradeBranchPattern
                                    postProcessing
                                    postProcessingConfig
                                    validationStamp
                                }
                            }
                        }
                    }
                `,
                {uuid}
            ).then(data => {
                const entries = data.autoVersioningAuditEntries.pageItems
                if (entries) {
                    setEntry(entries[0])
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, uuid]);

    return (
        <>
            <Head>
                {pageTitle("TODO")}
            </Head>
            <MainPage
                title={
                    <Space>
                        TODO
                    </Space>
                }
                breadcrumbs={[]} // TODO
                commands={[]} // TODO
            >
                <Skeleton active loading={loading}>
                    {
                        entry && <AutoVersioningAuditEntry entry={entry}/>
                    }
                    {
                        !entry && <Empty description="Audit entry not found"/>
                    }
                </Skeleton>
            </MainPage>
        </>
    )
}