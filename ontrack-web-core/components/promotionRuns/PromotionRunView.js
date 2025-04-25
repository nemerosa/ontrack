import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {buildKnownName, pageTitle, promotionLevelTitleName} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToBuildBreadcrumbs} from "@components/common/Breadcrumbs";
import {Card, Skeleton, Space, Typography} from "antd";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import {CloseCommand} from "@components/common/Commands";
import {buildUri} from "@components/common/Links";
import BuildLink from "@components/builds/BuildLink";
import TimestampText from "@components/common/TimestampText";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import PageSection from "@components/common/PageSection";
import AutoVersioningTrail from "@components/extension/auto-versioning/AutoVersioningTrail";
import {gqlAutoVersioningTrailContent} from "@components/extension/auto-versioning/AutoVersioningGraphQLFragments";
import {isAuthorized} from "@components/common/authorizations";
import ConfirmCommand from "@components/common/ConfirmCommand";
import {FaTrash} from "react-icons/fa";
import {useRouter} from "next/router";
import NotificationRecordingsTable from "@components/extension/notifications/NotificationRecordingsTable";

export default function PromotionRunView({id}) {

    const client = useGraphQLClient()
    const router = useRouter()

    const [loading, setLoading] = useState(true)
    const [run, setRun] = useState({})
    const [commands, setCommands] = useState([])

    const goToBuild = async () => {
        await router.push(buildUri(run.build))
    }

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query GetPromotionRun($id: Int!) {
                        promotionRuns(id: $id) {
                            id
                            description
                            annotatedDescription
                            creation {
                                user
                                time
                            }
                            authorizations {
                                name
                                action
                                authorized
                            }
                            build {
                                id
                                name
                                releaseProperty {
                                    value
                                }
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                            }
                            promotionLevel {
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
                            autoVersioningTrail {
                                ...AutoVersioningTrailContent
                            }
                        }
                    }

                    ${gqlAutoVersioningTrailContent}
                `,
                {id: Number(id)}
            ).then(data => {
                const promotionRun = data.promotionRuns[0];
                setRun(promotionRun)

                const commands = []
                if (isAuthorized(promotionRun, 'promotion_run', 'delete')) {
                    commands.push(
                        <ConfirmCommand
                            key="delete"
                            icon={<FaTrash/>}
                            text="Delete"
                            confirmTitle="Removing this promotion run"
                            confirmText="Do you really want to remove this promotion run?"
                            confirmOkText="Confirm deletion"
                            gqlQuery={
                                gql`
                                    mutation DeletePromotionRun($id: Int!) {
                                        deletePromotionRun(input: {promotionRunId: $id}) {
                                            errors {
                                                message                                            
                                            }
                                        }
                                    }
                                `
                            }
                            gqlVariables={{id: promotionRun.id}}
                            gqlUserNode="deletePromotionRun"
                            onSuccess={goToBuild}
                        />
                    )
                }
                commands.push(
                    <CloseCommand key="close" href={buildUri(promotionRun.build)}/>
                )
                setCommands(commands)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id])

    return (
        <>
            <Head>
                {
                    run?.promotionLevel &&
                    pageTitle(`${promotionLevelTitleName(run?.promotionLevel)} --> ${buildKnownName(run?.build)}`)
                }
            </Head>
            <MainPage
                title={
                    run?.promotionLevel && <>
                        <Space>
                            <Typography.Text>Promotion to</Typography.Text>
                            <PromotionLevelLink promotionLevel={run?.promotionLevel}/>
                        </Space>
                    </>
                }
                commands={commands}
                breadcrumbs={downToBuildBreadcrumbs(run)}
            >
                <Skeleton loading={loading} active>
                    <Space direction="vertical" className="ot-line">
                        <Card>
                            <Space direction="vertical">
                                <Typography.Paragraph>
                                    Build <BuildLink build={run.build}/> has been promoted to <PromotionLevelLink
                                    promotionLevel={run.promotionLevel}/>.
                                </Typography.Paragraph>
                                {
                                    run.description &&
                                    <Typography.Paragraph>
                                        <AnnotatedDescription entity={run}/>
                                    </Typography.Paragraph>
                                }
                                {
                                    run.creation &&
                                    <Typography.Paragraph>
                                        <Space>
                                            <Typography.Text type="secondary">Timestamp:</Typography.Text>
                                            <TimestampText value={run.creation.time}/>
                                            <Typography.Text disabled>
                                                ({run.creation.user})
                                            </Typography.Text>
                                        </Space>
                                    </Typography.Paragraph>
                                }
                            </Space>
                        </Card>
                        {
                            run?.autoVersioningTrail &&
                            <PageSection
                                title="Auto-versioning trail"
                            >
                                <AutoVersioningTrail trail={run.autoVersioningTrail}/>
                            </PageSection>
                        }
                        <PageSection
                            id="promotion-run-notifications"
                            title="Notifications"
                        >
                            <Typography.Paragraph type="secondary" style={{padding: 8}}>
                                List of notifications sent for this promotion.
                            </Typography.Paragraph>
                            <NotificationRecordingsTable
                                entity={{
                                    type: 'PROMOTION_RUN',
                                    id: run.id,
                                }}
                                sourceId="entity-subscription"
                            />
                        </PageSection>
                    </Space>
                </Skeleton>
            </MainPage>
        </>
    )
}